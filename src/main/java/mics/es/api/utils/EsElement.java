package mics.es.api.utils;

public class EsElement {

    public boolean isEsObject(){return this instanceof EsObject;}

    public boolean isEsBulk(){return this instanceof EsBulk;}

    public EsObject getAsEsObject(){
        if (isEsObject()){
            return (EsObject)this;
        }
        throw new IllegalStateException("Not a EsObject");
    }
}
