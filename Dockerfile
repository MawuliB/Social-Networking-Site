FROM node:current-alpine3.18

# Create app directory
WORKDIR /app
COPY package*.json ./
RUN npm install -g @angular/cli
RUN npm install

# Bundle app source
COPY . .

CMD [ "ng", "serve", "--host", "0.0.0.0" ]