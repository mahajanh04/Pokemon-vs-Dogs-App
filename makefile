#Makefile

# frontend start dir
FRONTEND_DIR=./frontend/apapung

# backend start dir
BACKEND_DIR=./backend/apapung

install:
	cd ${FRONTEND_DIR} && npm install

# use `make front` to run a frontend app
front:
	cd $(FRONTEND_DIR) && npm run dev

# use `make back` to run a backend server
back:
	cd $(BACKEND_DIR) && mvn spring-boot:run

# use `make dev` to run the whole application
dev:
	cd $(FRONTEND_DIR) && npm run dev & \
	cd $(BACKEND_DIR) && mvn spring-boot:run & \
	wait