package kiradk.client.utils

public class MutableRange<T : Comparable<T>>(
    public var start: T,
    public var end: T,
) {

    override fun toString(): String {
        return "MutableRange(start=$start, end=$end)"
    }
}

public class MutableIntRange(
    public var start: Int,
    public var end: Int
) {
    override fun toString(): String {
        return "MutableIntRange(start=$start, end=$end)"
    }
}
