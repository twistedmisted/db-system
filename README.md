# Database Management Tool
This project is a web-based application for managing databases, tables, and providing an API to operate on data within those tables. It enables users to easily create, edit, and delete databases and tables, with an intuitive UI for handling columns and constraints. The tool also supports CRUD operations via a REST API for interacting with data in the created tables.

## Features
### User Features:
- **User Authentication:** Register and log in with JWT-based authentication.
- **Database Management:** Create, edit, and delete multiple databases.
- **Table Management**: For each database, users can:
  - Create, edit, and delete tables.
  - Add, edit, and delete columns (with constraint support).
  - Modify column types and other table constraints.
- **API for CRUD Operations:** After database and table creation, users can perform basic CRUD (Create, Read, Update, Delete) operations on the data in the tables via an API.
- **Future Features (Planned):**
  - Custom query support for tables.
  - Extended constraint and column type options.

## Tech Stack

### Backend:
- **Java 21 & Spring Boot 3:** The backend is built using Spring Boot, which handles all business logic, database operations, and API endpoints.
- **InterSystems IRIS:** Primary database used for storing and managing the databases and tables created by users.
- **Redis:** Used for caching to improve performance.
- **JWT Authentication:** Secure access to both the UI and API endpoints, with separate tokens for UI access and API access.
- 
### Frontend:
- **Angular 17:** For building the user interface and managing dynamic forms for interacting with databases and tables.

## Installation

1. Open source folder
2. Run command:`docker-compose up`
3. Everything done

Server address - `http://localhost:8080`

Client address - `http://localhost:4200/`
