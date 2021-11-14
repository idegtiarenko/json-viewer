# json-viewer

![preview](preview.png)

Simple application to visualize (list keys and preview nodes) big json files.

## Development

Development requires JDK 17 with preview features enabled.

* Run application: `./gradlew run` or `./gradlew run --args='${pathToJsonFile}'`
* Create distribution (with bundled jre): `./gradlew jlink` (available in `./build/image`)

## Distribution

Distribution does not have external dependencies and is shipped with jre.

* Start distribution (from `./build/image` or downloaded zip) `./bin/json-viewer` or `./bin/json-viewer ${pathToJsonFile}`
