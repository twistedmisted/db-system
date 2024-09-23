FROM node:22.8.0

WORKDIR /app

COPY . .
RUN npm install
RUN npm install -g @angular/cli --force

CMD ["ng", "serve", "--host", "0.0.0.0"]
