import csv
import json
import requests

url = "http://localhost:8080/api/v1/customer/add"

with open('customer_data.csv', 'r') as file:
    reader = csv.DictReader(file)

    for row in reader:
        books = json.loads(row["books"])
        listOfBooks = []

        for book in books:
            listOfBooks.append({
                "book_id": book["book_id"],
                "author_name": book["author_name"],
                "book_name": book["book_name"],
                "lend_date": book["lend_date"],
                "days_to_return": int(book["days_to_return"])
            })

        customer_data_request_dto = {
            "customerId": int(row["customer_id"]),
            "customerName": row["customer_name"],
            "listOfBooks": listOfBooks
        }

        payload = json.dumps(customer_data_request_dto)

        response = requests.post(url, data=payload, headers={'Content-Type': 'application/json'})

        print(response.text)