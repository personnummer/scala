# personnummer [![Build Status](https://github.com/personnummer/scala/workflows/test/badge.svg)](https://github.com/personnummer/scala/actions)

Validate Swedish personal identity numbers.

## Example

```scala
import personnummer.Personnummer

Personnummer.valid("198507099805")
//=> true
```

## Test locally with docker

```
docker run -it --rm -v ~/.ivy2:/root/.ivy2 -v ~/.sbt:/root/.sbt -v $PWD:/app -w /app mozilla/sbt sbt test
```

## In memoriam

Fredrik "Frozzare" Forsmo (1991-2026) was the initiator, co-founder and a core contributor of the personnummer project. This library carries his work. He is missed.

## License

MIT
