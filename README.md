# guestbook

generated using Luminus version "3.32"

FIXME

## Prerequisites

You will need [Leiningen][1] 2.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein run


## Deploying with Docker
1. `lein uberjar`
2. `sudo docker build -t guestbook .`
2. `sudo dcrun up -d --build --no-deps guestbook`
3. `sudo docker exec -it guestbook /bin/sh` - Just to look at the log...you should have that mapped in your docker-config
4.

## Useful links
- http://www.luminusweb.net/docs/guestbook.html
- https://circleci.com/blog/package-a-clojure-web-application-using-docker/
- https://practicalli.github.io/clojure-webapps/


## License

Copyright © 2019 FIXME
