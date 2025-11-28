# 📋 COMANDOS PARA EJECUTAR TESTS DESDE TERMINAL (Git Bash)

## 🚀 COMANDOS BÁSICOS

### 1. **Ejecutar TODOS los tests del proyecto**
```bash
./mvnw test
```

### 2. **Ejecutar tests con salida detallada**
```bash
./mvnw test -X
```

### 3. **Ejecutar tests en modo offline**
```bash
./mvnw test -o
```

---

## 🎯 EJECUTAR UN TEST ESPECÍFICO

### 4. **Ejecutar una clase de test completa**
```bash
./mvnw test -Dtest=UserServiceTest
./mvnw test -Dtest=MateriaServiceTest
./mvnw test -Dtest=EvaluacionServiceTest
```

### 5. **Ejecutar un método de test específico**
```bash
./mvnw test -Dtest=UserServiceTest#register_exito
./mvnw test -Dtest=MateriaServiceTest#getMateria_retornaDTO_siExisteMateria
./mvnw test -Dtest=EvaluacionServiceTest#getEvaluacion_debeRetornarDTO_siExiste
```

### 6. **Ejecutar múltiples clases específicas**
```bash
./mvnw test -Dtest=UserServiceTest,MateriaServiceTest,EvaluacionServiceTest
```

---

## 📊 OPCIONES AVANZADAS

### 7. **Ejecutar tests omitiendo fallos (continúa aún si hay errores)**
```bash
./mvnw test -DtestFailureIgnore=true
```

### 8. **Saltar los tests durante la construcción**
```bash
./mvnw clean install -DskipTests
```

### 9. **Ejecutar tests solo de integración**
```bash
./mvnw test -Dtest=**/*IntegrationTest
```

### 10. **Ejecutar tests en paralelo (más rápido)**
```bash
./mvnw test -DparallelizeModule -DparallelizeMethods
```

---

## 🔍 PATRONES Y FILTROS

### 11. **Ejecutar tests que coincidan con un patrón**
```bash
./mvnw test -Dtest=*Service*
./mvnw test -Dtest=*Repository*
```

### 12. **Ejecutar tests excluyendo algunos**
```bash
./mvnw test -Dtest=*Service* -DexcludedGroups=slow
```

---

## 📈 GENERAR REPORTES

### 13. **Ejecutar tests y generar reporte HTML**
```bash
./mvnw test surefire-report:report
```

### 14. **Ver reporte en navegador (después de generar)**
```bash
./mvnw surefire-report:report-only
```

---

## 🧹 LIMPIAR Y EJECUTAR

### 15. **Limpiar y ejecutar todos los tests**
```bash
./mvnw clean test
```

### 16. **Limpiar, compilar y ejecutar tests**
```bash
./mvnw clean compile test
```

### 17. **Limpiar y ejecutar un test específico**
```bash
./mvnw clean test -Dtest=UserServiceTest
```

---

## 📝 LISTA DE TESTS DISPONIBLES

| Archivo | Ubicación | Tests |
|---------|-----------|-------|
| UserServiceTest | `src/test/java/com/example/backend/` | 17 tests |
| MateriaServiceTest | `src/test/java/com/example/backend/` | 25+ tests |
| EvaluacionServiceTest | `src/test/java/com/example/backend/` | 6+ tests |
| SemestreServiceTest | `src/test/java/com/example/backend/` | 8+ tests |
| GestionServiceTest | `src/test/java/com/example/backend/` | 3+ tests |
| JwtServiceTest | `src/test/java/com/example/backend/` | 4+ tests |
| MatriculacionServiceTest | `src/test/java/com/example/backend/` | 5+ tests |
| ModalidadServiceTest | `src/test/java/com/example/backend/` | Tests |
| NotaServiceTest | `src/test/java/com/example/backend/` | Tests |
| SemestreMateriaServiceTest | `src/test/java/com/example/backend/` | Tests |
| EvaluacionRepositoryIntegrationTest | `src/test/java/com/example/backend/repository/` | Tests |

---

## 🎓 EJEMPLOS PRÁCTICOS

### Ejecutar solo tests de Usuario
```bash
./mvnw test -Dtest=UserServiceTest
```

### Ejecutar solo tests de Materia
```bash
./mvnw test -Dtest=MateriaServiceTest
```

### Ejecutar un test específico de Materia
```bash
./mvnw test -Dtest=MateriaServiceTest#getMateria_retornaDTO_siExisteMateria
```

### Ejecutar tests de UserService y MateriaService
```bash
./mvnw test -Dtest=UserServiceTest,MateriaServiceTest
```

### Ejecutar todos los tests y generar reporte
```bash
./mvnw clean test surefire-report:report
```

### Ejecutar tests ignorando fallos
```bash
./mvnw test -DtestFailureIgnore=true
```

---

## 💡 TIPS ÚTILES

- **Antes de ejecutar tests**, asegúrate de estar en el directorio correcto: `X:\taller\TallerVBackend-master\backend`
- **Usa `./mvnw.cmd`** en lugar de `./mvnw` si estás en cmd.exe (no en Git Bash)
- **Usa `./mvnw`** en Git Bash (lo que recomiendamos)
- **Primero limpia**: `./mvnw clean` antes de ejecutar tests
- **Para ver errores detallados**: Agrega `-e` al comando

---

## 📍 COMANDO RECOMENDADO PARA EMPEZAR

```bash
cd X:\taller\TallerVBackend-master\backend
./mvnw clean test
```

Este comando limpiará el proyecto y ejecutará todos los tests mostrando los resultados.

