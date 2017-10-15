[![Download](https://api.bintray.com/packages/novacrypto/BIP/Base58/images/download.svg)](https://bintray.com/novacrypto/BIP/Base58/_latestVersion) [![Build Status](https://travis-ci.org/NovaCrypto/Base58.svg?branch=master)](https://travis-ci.org/NovaCrypto/Base58) [![codecov](https://codecov.io/gh/NovaCrypto/Base58/branch/master/graph/badge.svg)](https://codecov.io/gh/NovaCrypto/Base58)


# Install

```
repositories {
    maven {
        url 'https://dl.bintray.com/novacrypto/BIP/'
    }
}
```

(`jcenter()` coming soon)

Add dependency:

```
dependencies {
    compile 'io.github.novacrypto:Base58:0.0.1@jar'
}

```

# Usage

From simplest to most advanced:

## Encode (static method)

```
String base58 = Base58.encodeStatic(bytes);
```

## Decode (static method)

```
byte[] bytes = Base58.decodeStatic(base58String);
```

The static methods are threadsafe and share a buffer per thread.

## Encode (instance method)

```
String base58 = new Base58().encode(bytes);
```

## Decode (instance method)

```
byte[] bytes = new Base58().decode(base58CharSequence);
```

The instances are not threadsafe, never share an instance across threads.

## Encode (to a target, instance method)

```
final StringBuilder sb = new StringBuilder();
encode(bytes, sb::append);
return sb.toString();
```

## Decode (to a target, instance method)

```
static class ByteArrayTarget implements DecodeTarget {
    private int idx = 0;
    byte[] bytes;

    @Override
    public DecodeWriter getWriterForLength(int len) {
        bytes = new byte[len];
        return b -> bytes[idx++] = b;
    }
}

ByteArrayTarget target = new ByteArrayTarget();
decode(base58, target);
target.bytes;
```

These advanced usages avoid allocating memory and allow [SecureByteBuffer](https://github.com/NovaCrypto/SecureString/blob/master/src/main/java/io/github/novacrypto/SecureByteBuffer.java) usage.
