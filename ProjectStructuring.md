# Project Structuring

```
bennycar/
├── .env                          # Environment variables (not in git)
├── .env.example                  # Environment template
├── .gitignore                    # Git ignore rules
├── .dockerignore                 # Docker ignore rules
├── docker-compose.yaml           # Main docker compose config
├── docker-compose.override.yml.example
├── init-db.sql                   # Database initialization
├── pom.xml                       # Root Maven config
├── mvnw / mvnw.cmd              # Maven wrapper
│
├── README.md                     # Architecture & concepts
├── PORTS.md                      # Port documentation
├── QUICKSTART.md                 # Quick start guide
│
├── user-service/                 # User microservice
│   ├── Dockerfile
│   ├── .dockerignore
│   ├── pom.xml                   # Standalone config
│   └── src/
│       ├── main/
│       │   ├── java/de/bennycar/user/
│       │   │   ├── UserServiceApplication.java
│       │   │   ├── controller/
│       │   │   ├── service/
│       │   │   ├── repository/
│       │   │   ├── model/
│       │   │   ├── dto/
│       │   │   └── security/
│       │   └── resources/
│       │       └── application.yml
│       └── test/
│
└── frontend/                     # React frontend
    ├── Dockerfile
    ├── package.json
    └── src/

```
