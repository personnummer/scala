# personnummer [![Build Status](https://github.com/personnummer/lua/workflows/test/badge.svg)](https://github.com/personnummer/lua/actions)

Validate Swedish personal identity numbers. Follows version 3 of the [specification](https://github.com/personnummer/meta#package-specification-v3).

## Example

```scala
import personnummer.Personnummer

Personnummer.valid("198507099805")
//=> true
```

## License

MIT
