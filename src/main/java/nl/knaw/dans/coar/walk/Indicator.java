package nl.knaw.dans.coar.walk;

public class Indicator
{
    
    private String key;
    private int line;
    private int page;
    private String value;
    
    public Indicator(String key, int line, int page) {
        this.key = key;
        this.line = line;
        this.page = page;
    }
    
    public String getIndicatorKey()
    {
        return key;
    }
    public void setKey(String key)
    {
        this.key = key;
    }
    public int getLine()
    {
        return line;
    }
    public void setLine(int line)
    {
        this.line = line;
    }
    public int getPage()
    {
        return page;
    }
    public void setPage(int page)
    {
        this.page = page;
    }
    public String getValue()
    {
        return value;
    }
    public void setValue(String value)
    {
        this.value = value;
    }
    
    
}
