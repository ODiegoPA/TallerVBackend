package com.example.backend;

import com.example.backend.dto.user.*;
import com.example.backend.models.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.JwtService;
import com.example.backend.service.UserService;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;

    @InjectMocks private UserService userService;

    private RegisterRequest registerReq;
    private User existing;

    @BeforeEach
    void setup() {
        registerReq = new RegisterRequest("Juan", "Perez", "juan@example.com", "pass", "123456", "Estudiante");
        existing = new User();
        existing.setId(1L);
        existing.setEmail("juan@example.com");
        existing.setPassword("encoded");
        existing.setNombre("Juan");
        existing.setApellido("Perez");
        existing.setRol("Estudiante");
    }

    @Test
    void register_exito() {
        when(userRepository.existsByEmail(registerReq.email())).thenReturn(false);
        when(passwordEncoder.encode(registerReq.password())).thenReturn("encoded");
        when(userRepository.existsByCodigo(anyString())).thenReturn(false); // para generar código
        when(jwtService.generateToken(any())).thenReturn("access" );
        when(jwtService.generateRefreshToken(registerReq.email())).thenReturn("refresh");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0); u.setId(99L); return u; });

        AuthResponse res = userService.register(registerReq);
        assertThat(res.token()).isEqualTo("access");
        assertThat(res.email()).isEqualTo("juan@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_fallaSiEmailExiste() {
        when(userRepository.existsByEmail(registerReq.email())).thenReturn(true);
        assertThatThrownBy(() -> userService.register(registerReq))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email");
    }

    @Test
    void register_fallaSiCodigoNoSePuedeGenerar() {
        when(userRepository.existsByEmail(registerReq.email())).thenReturn(false);
        when(passwordEncoder.encode(registerReq.password())).thenReturn("encoded");
        // fuerza a que generateAlumnoCodeUnique falle
        when(userRepository.existsByCodigo(anyString())).thenReturn(true);
        assertThatThrownBy(() -> userService.register(registerReq))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No se pudo generar");
    }

    @Test
    void login_exito() {
        when(userRepository.findByEmail(existing.getEmail())).thenReturn(Optional.of(existing));
        when(passwordEncoder.matches("pass", existing.getPassword())).thenReturn(true);
        when(jwtService.generateToken(any())).thenReturn("access");
        when(jwtService.generateRefreshToken(existing.getEmail())).thenReturn("refresh");
        AuthResponse res = userService.login(new LoginRequest(existing.getEmail(), "pass"));
        assertThat(res.token()).isEqualTo("access");
    }

    @Test
    void login_usuarioNoExiste() {
        when(userRepository.findByEmail("no@existe.com")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.login(new LoginRequest("no@existe.com", "x")))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void login_passwordIncorrecto() {
        when(userRepository.findByEmail(existing.getEmail())).thenReturn(Optional.of(existing));
        when(passwordEncoder.matches("mal", existing.getPassword())).thenReturn(false);
        assertThatThrownBy(() -> userService.login(new LoginRequest(existing.getEmail(), "mal")))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void refresh_exito() {
        when(jwtService.extractUsernameFromRefresh("r1")).thenReturn(existing.getEmail());
        when(userRepository.findByEmail(existing.getEmail())).thenReturn(Optional.of(existing));
        when(jwtService.isRefreshValid("r1", existing.getEmail())).thenReturn(true);
        when(jwtService.generateToken(any())).thenReturn("access2");
        when(jwtService.generateRefreshToken(existing.getEmail())).thenReturn("refresh2");

        AuthResponse res = userService.refresh(new RefreshRequest("r1"));
        assertThat(res.token()).isEqualTo("access2");
        assertThat(res.refreshToken()).isEqualTo("refresh2");
    }

    @Test
    void refresh_tokenInvalidoParse() {
        when(jwtService.extractUsernameFromRefresh("bad")).thenThrow(new JwtException("err"));
        assertThatThrownBy(() -> userService.refresh(new RefreshRequest("bad")))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void refresh_tokenInvalidoValidacion() {
        when(jwtService.extractUsernameFromRefresh("r1")).thenReturn(existing.getEmail());
        when(userRepository.findByEmail(existing.getEmail())).thenReturn(Optional.of(existing));
        when(jwtService.isRefreshValid("r1", existing.getEmail())).thenReturn(false);
        assertThatThrownBy(() -> userService.refresh(new RefreshRequest("r1")))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void me_lanzaSiAuthNull() {
        assertThatThrownBy(() -> userService.me(null))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void me_lanzaSiNoAutenticado() {
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(false);
        assertThatThrownBy(() -> userService.me(auth))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void me_ok() {
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("juan@example.com");
        Collection<? extends GrantedAuthority> col = List.of(new SimpleGrantedAuthority("ROLE_Estudiante"));
        doReturn(col).when(auth).getAuthorities();
        Map<String,Object> map = userService.me(auth);
        @SuppressWarnings("unchecked")
        List<String> authorities = (List<String>) map.get("authorities");
        assertThat(map.get("name")).isEqualTo("juan@example.com");
        assertThat(authorities).contains("ROLE_Estudiante");
    }

    @Test
    void getAllUsersByRole_nullReq() {
        User u1 = new User(); u1.setEmail("a@a");
        when(userRepository.findAll()).thenReturn(List.of(u1));
        List<UserDto> lista = userService.getAllUsersByRole(null);
        assertThat(lista).hasSize(1);
    }

    @Test
    void getAllUsersByRole_rolVacio() {
        User u1 = new User(); u1.setEmail("a@a");
        when(userRepository.findAll()).thenReturn(List.of(u1));
        List<UserDto> lista = userService.getAllUsersByRole(new RolRequestDto(" "));
        assertThat(lista).hasSize(1);
    }

    @Test
    void getAllUsersByRole_filtrado() {
        User u1 = new User(); u1.setEmail("a@a"); u1.setRol("Docente");
        when(userRepository.findAllByRol("Docente")).thenReturn(List.of(u1));
        List<UserDto> lista = userService.getAllUsersByRole(new RolRequestDto("Docente"));
        assertThat(lista).hasSize(1);
        assertThat(lista.get(0).rol()).isEqualTo("Docente");
    }

    @Test
    void loadUserByUsername_ok() {
        when(userRepository.findByEmail(existing.getEmail())).thenReturn(Optional.of(existing));
        assertThat(userService.loadUserByUsername(existing.getEmail()).getUsername()).isEqualTo(existing.getEmail());
    }

    @Test
    void loadUserByUsername_noExiste() {
        when(userRepository.findByEmail("x@x")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.loadUserByUsername("x@x"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}

