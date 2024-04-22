## Tech Notes
1. Postgres image from docker registry is used as the database.


## For running the project locally, follow the steps below:
1. For migrating the csv data to database, run the following command:
```python3 migrateCSVDataToDb.py```
Make sure that the csv file and python script are at same location.

2. Run the command below to start postgres:
```docker-compose up -d```

3. Run the application to create the tables.

4. For running migration scrips, please run the commands below:
```
 docker build -t my-flyway-image .
 docker run --rm --network="host" my-flyway-image migrate
```