# todo

A simple todo web application in clojure.

This project is only the backend for application, & exposes API's
for the client.

The server side uses <em>mongodb</em> as database using <em> monger</em> library to connect to the database.

The server returns a json response to client queries.

## Prerequisites

You will need [Leiningen][] 2.0.0 or above installed.

[leiningen]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein ring server

## License

Copyright © 2017 FIXME
