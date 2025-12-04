# apapung
This project was developed in tandem with the DUCCS/SULU hackathon.<br>
This allowed our team access to a wide selection of APIs to incorporate into our project.<br>
Accessed from https://sparkhub.sulu.sh/

### Github username allocations:
Swelltyz = Ben Mooney Byrne / mooneybb<br>
Ktoettotakoy = Yaroslav Kashulin / kashuliy<br>
liuphui = Peng Hui Liu / liup1<br>
Yaqi66 = Yaqi Yang / yangy10 (Second year who partook in the the hackathon with us)<br>

### Structure:
Our application is built consisting of a next.js frontend and a Springboot backend.

### Our application:
Our application allows users to pit Pokémon and dogs against each other in a battling system.
The number of dogs (of a user-determined breed) required to fight a (user-determined) Pokémon will be calculated.
The price of the dogs will be calculated. This price will then be matched against the cost of different Amazon.com products filtered by type.

### To run an app on local machine
1. Have docker installed

2. Run ```git clone https://github.com/Ktoettotakoy/apapung.git```

3. Create an .env file in /backend/apapung folder

4. Add the following 3 tokens to .env:
- SULU_TOKEN (we used https://platform.sulu.sh/)
- DOG_API_TOKEN (we used https://www.thedogapi.com/)
- OPENAI_TOKEN (we used https://console.groq.com/docs/api-reference#chat-create)

5. Run ```docker-compose up --build``` from root directory of the project

6. Navigate to ```http://localhost:3000```