[![Download](https://api.bintray.com/packages/novacrypto/BIP/Base58/images/download.svg)](https://bintray.com/novacrypto/BIP/Base58/_latestVersion) [![Build Status](https://travis-ci.org/NovaCrypto/Base58.svg?branch=master)](https://travis-ci.org/NovaCrypto/Base58) [![codecov](https://codecov.io/gh/NovaCrypto/Base58/branch/master/graph/badge.svg)](https://codecov.io/gh/NovaCrypto/Base58)

# Install

Use either of these repositories:

```
repositories {
    jcenter()
}
```

Or:

```
repositories {
    maven {
        url 'https://dl.bintray.com/novacrypto/BIP/'
    }
}
```

Add dependency:

```
dependencies {
    compile 'io.github.novacrypto:Base58:0.1.2@jar'
}

```

# Usage

From simplest to most advanced:

## Encode (static method)

```
String base58 = Base58.base58Encode(bytes);
```

## Decode (static method)

```
byte[] bytes = Base58.base58Decode(base58String);
```

The static methods are threadsafe as they have a shared buffer per thread. They are named so they are still readable if you `import static`.

## Encode (instance method)

```
String base58 = Base58.newInstance().encode(bytes);
```

## Decode (instance method)

```
byte[] bytes = Base58.newInstance().decode(base58CharSequence);
```

The instances are not threadsafe, never share an instance across threads.

## Encode (to a target, instance method)

```
final StringBuilder sb = new StringBuilder();
Base58.newSecureInstance().encode(bytes, sb::append);
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
Base58.newSecureInstance().decode(base58, target);
target.bytes;
```

These advanced usages avoid allocating memory and allow [SecureByteBuffer](https://github.com/NovaCrypto/SecureString/blob/master/src/main/java/io/github/novacrypto/SecureByteBuffer.java) usage.
