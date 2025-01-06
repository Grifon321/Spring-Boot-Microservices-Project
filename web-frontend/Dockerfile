FROM node:20-alpine
WORKDIR /app
COPY . .

RUN npm install && npm install -g @ui5/cli

EXPOSE 8080
CMD ["npm", "run", "serve"]