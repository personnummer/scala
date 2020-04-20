# personnummer

Validate Swedish social security numbers. Follows version 3 of the [specification](https://github.com/personnummer/meta#package-specification-v3).

## Example

```scala
import personnummer.Personnummer

Personnummer.valid("198507099805")
//=> true
```

## License

MIT