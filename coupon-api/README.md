# Bilyoner Assigment 
Bilyoner assignment is a project consists of two modules; couponEntity-api and balance-api. The project is implemented 
according to the rules listed in BilyonerHazırKuponOynat.pdf file. 

# What we want from you
    - Implement missing code blocks in these projects
    - Write unit/integration tests
    * Refactor complex functions (It's optional)

# Notes 
    - Consider that DBInitializerService classes are populating database with sample eventEntity and user information 
      during start of services.
    - You can use swagger-ui or attached postman collection to test and use sample api calls.
# Tests
    - Run only unit tests : mvn test -DskipIntegrationTests
    - Run only integration tests : mvn failsafe:integration-test
    - Run both : mvn verify or mvn exec:exec