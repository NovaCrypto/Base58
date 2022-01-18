[![Maven Central](https://img.shields.io/maven-central/v/io.github.novacrypto/Base58)](https://search.maven.org/artifact/io.github.novacrypto/Base58/)

# Install

Using:

```groovy
repositories {
    mavenCentral()
}
```

Add dependency:

```groovy
dependencies {
    implementation 'io.github.novacrypto:Base58:2022.01.17@jar'
}
```

# Usage

From simplest to most advanced:

## Encode (static method)

```java
String base58 = Base58.base58Encode(bytes);
```

## Decode (static method)

```java
byte[] bytes = Base58.base58Decode(base58String);
```

The static methods are threadsafe as they have a shared buffer per thread. They are named so they are still readable if you `import static`.

## Encode (instance method)

```java
String base58 = Base58.newInstance().encode(bytes);
```

## Decode (instance method)

```java
byte[] bytes = Base58.newInstance().decode(base58CharSequence);
```

The instances are not threadsafe, never share an instance across threads.

## Encode (to a target, instance method)

Either:

```java
final StringBuilder sb = new StringBuilder();
Base58.newSecureInstance().encode(bytes, sb::append);
return sb.toString();
```

Or let it get told the correct initial maximum size:

```java
final StringBuilder sb = new StringBuilder();
Base58.newSecureInstance().encode(bytes, sb::ensureCapacity, sb::append);
return sb.toString();
```

Or supply an implementation of `EncodeTargetFromCapacity`:

```java
final StringBuilder sb = new StringBuilder();
Base58.newSecureInstance().encode(bytes, (charLength) -> {
    // gives you a chance to allocate memory before passing the buffer as an EncodeTarget
    sb.ensureCapacity(charLength);
    return sb::append; // EncodeTarget
});
return sb.toString();
```

## Decode (to a target, instance method)

```java
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

# Change Log

## 0.1.3

- Update dependencies
- Add `EncodeTargetFromCapacity` and `EncodeTargetCapacity` interfaces and related `SecureEncoder#encode` method overloads

## 2022.01.17

- uses static `SecureRandom` on the advice of Spotbugs, and while it was a false positive intended for `Random` use warning, it's not a bad thing to do anyway.
