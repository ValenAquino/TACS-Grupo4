.PHONY: up build down logs logs-back logs-front dev dev-down deploy-back test

# --- Prod ---

up:
	docker compose up -d

build:
	docker compose up --build -d

down:
	docker compose down

logs:
	docker compose logs -f

logs-back:
	docker compose logs -f backend

logs-front:
	docker compose logs -f frontend

# --- Dev ---

dev:
	docker compose -f docker-compose.dev.yml up --build --watch

dev-down:
	docker compose -f docker-compose.dev.yml down

deploy-back:
	docker compose -f docker-compose.dev.yml up --build backend

# --- Tests ---

test:
	docker compose -f docker-compose.dev.yml run --rm backend mvn test
