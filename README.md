# json-zero

`json-zero` is a streaming json parser library. It uses recursive descending algorithm, with the following features:
 - zero copying. It doesn't copy the incoming data, just notifies the handler with the bounds of the elements observed.
 - zero allocation. It doesn't allocate any objects on the heap.

`json-zero` also provides a high level interface, that can be used to quickly parse json in java objects. It supports parsing into map/lists, as well as in custom java objects. Also, it can be enhanced to support item specific parsing.

## Usage

### Using the parser directly

```java
Parser.parse("{\"hello\": \"world\"}".toCharArray(), new DefaultContentHandler() {
    @Override
    public boolean beginObjectEntry(char[] source, int begin, int end, int escapeCount) throws ParseException {
        System.out.print(readString(source, begin, end, escapeCount));
        return true;
    }

    @Override
    public boolean stringValue(char[] source, int begin, int end, int escapeCount) throws ParseException {
        System.out.print(" ");
        System.out.println(readString(source, begin, end, escapeCount));
        return false;
    }
});
```

### Using the high-level interface

```java
var result = JsonParser.parse("{\"hello\": \"world\"}".toCharArray(), 
    new JsonMapHandler<String>(new JsonStringHandler()));
for (var e : result.entrySet()) {
    System.out.format("%s %s", e.getKey(), e.getValue());
}
```
