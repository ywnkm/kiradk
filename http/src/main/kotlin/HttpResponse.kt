public class HttpResponse(
    public val method:Method,
    public val version:Version,
    public val resource:Resource,
    public val headers:HashMap<String, String>,
    public val msgBody:String
)

public enum class Method{
    POST,
    GET,
    Uninitialized
}


public enum class Version{
    V1_1,
    Uninitialized
}

public data class Resource(
    val path: Path
){
    public data class Path(
        val string: String
    )
}