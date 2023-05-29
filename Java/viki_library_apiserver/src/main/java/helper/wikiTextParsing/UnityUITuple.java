package helper.wikiTextParsing;

public class UnityUITuple {

    public UnityUITuple(UnityUIElement element, String content){
        this.element = element;
        this.content = content;
    }

    private UnityUIElement element;
    private String content;

    public UnityUIElement getElement(){
        return element;
    }

    public String getContent(){
        return content;
    }
}

