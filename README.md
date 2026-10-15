# Kata Checkout

This is a system for checkout. You'll be able to set your own:

- Items and prices
- Rules
- Cart

And as result, get the total value, in pence.

## How to run

The system can be ran both locally of with Docker.
The API will be exposed at `http://localhost:8080/`

To try the API, you can import [docs/kata-checkout.postman_collection.json](docs/kata-checkout.postman_collection.json) into Postman — it contains example requests with response assertions.

There is also a simple web UI at `http://localhost:8080/` for cashier-style checkouts where you can setup up items, prices, cart and check final value.

### Docker

- Requires Docker

```bash
docker compose up --build
```

### Local

- Requires Java 21

```bash
./gradlew bootRun
```

## Requirements

- Calculate total price of several items
- Items identified as individual letters
- Priced individually
- Promotions applied
- Items in aby order
- Extra: be able to receive set of rules for each checkout transaction.

## Technical Decisions

- HTTP API for communication
- Service following simple layered architecture
- Using Kotlin and Springboot
- No need for database
- Rules will be applied in order received
