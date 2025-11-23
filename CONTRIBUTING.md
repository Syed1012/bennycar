# Contributing to Bennycar

Thank you for your interest in contributing to the Bennycar vehicle purchasing platform!

## 🚀 Quick Start for Contributors

1. **Fork the repository**
2. **Clone your fork**
   ```bash
   git clone <your-fork-url>
   cd bennycar
   ```
3. **Set up environment**
   ```bash
   cp .env.example .env
   # Edit .env with your local configuration
   ```
4. **Start development environment**
   ```bash
   docker-compose up -d postgres
   ```

## 📋 Development Guidelines

### Code Style
- **Java**: Follow Spring Boot conventions
- **Formatting**: Use IDE auto-formatting
- **Naming**: Use descriptive variable/method names
- **Comments**: Document complex logic

### Git Workflow
1. Create a feature branch
   ```bash
   git checkout -b feature/your-feature-name
   ```
2. Make your changes
3. Test thoroughly
4. Commit with meaningful messages
   ```bash
   git commit -m "feat: add user profile endpoint"
   ```
5. Push and create Pull Request

### Commit Message Format
```
<type>: <description>

Types:
- feat: New feature
- fix: Bug fix
- docs: Documentation changes
- refactor: Code refactoring
- test: Adding tests
- chore: Maintenance tasks
```

## 🧪 Testing

### Run Tests
```bash
cd user-service
mvn test
```

### Integration Tests
```bash
docker-compose up -d
mvn verify
```

## 📝 Adding a New Microservice

1. Create service directory: `<service-name>/`
2. Add `Dockerfile` and `.dockerignore`
3. Create standalone `pom.xml`
4. Add to `docker-compose.yaml`
5. Create database schema in `init-db.sql`
6. Update documentation

## 🔍 Code Review Checklist

- [ ] Code follows project conventions
- [ ] All tests pass
- [ ] Documentation updated
- [ ] No hardcoded credentials
- [ ] Environment variables used
- [ ] Dockerfile optimized
- [ ] API endpoints documented

## 🐛 Reporting Bugs

Include:
- Description of the bug
- Steps to reproduce
- Expected vs actual behavior
- Environment details
- Relevant logs

## 💡 Suggesting Features

- Check existing issues first
- Describe the feature clearly
- Explain the use case
- Provide examples if possible

## 📞 Questions?

- Check documentation in `/docs`
- Review existing issues
- Ask in discussions

---

**Thank you for contributing! 🎉**

