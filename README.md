# test-javascript-web-evaluator

Made for testing webdriver impelmentations. 

Creates a web server that can reproduce various interesting behaviors related to getting page html output of a page that has javascript rendered contents.

Build the fat jar with gradle.

Then run it:
`java -jar test-javascript-web-evaluator/build/libs/test-javascript-web-evaluator-1.0-SNAPSHOT.jar 7001`

where 7001 is the port number. 

Requests can be:

`/` or `/index.html` - Will present a page with 10,000 random pages (see below)

`page*` - will be a page with 3 ajax requests each with random wait time between 0 and 20.

`infinite*` - will present a page with a javascript infinite loop

`pagetimeout*` - will sleep 10000 seconds thus timing out.

`ajaxtimeout*` - will have an ajax request that times out.

`ajax*` - will have an ajax request that will take a random time between 1 and 20 seconds to return.
