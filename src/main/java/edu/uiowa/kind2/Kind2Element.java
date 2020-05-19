package edu.uiowa.kind2;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Kind2Element
{
    private final String json;
    private final JsonElement jsonElement;
    private final String category;
    private final String jsonName;
    private final String name;
    private final String qualifiedName;
    private final long line;
    private final long column;
    private final Kind2Node kind2Node;
    private final Kind2Property kind2Property;

    public Kind2Element(Kind2Node kind2Node, JsonElement jsonElement)
    {
        this.kind2Node = kind2Node;
        this.jsonElement = jsonElement;
        json = new GsonBuilder().setPrettyPrinting().create().toJson(jsonElement);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        jsonName = jsonObject.get(Kind2Labels.name).getAsString();
        category = jsonObject.get(Kind2Labels.category).getAsString();
        if(category.equals(Kind2Labels.equation))
        {
            // equation does not correspond to a property
            kind2Property = null;
            name = jsonName;
        }
        else
        {
            // get the corresponding property
            kind2Property = getKind2Analysis().getProperty(jsonName);
            name = jsonName.replaceAll("\\[.*?\\]", "").replaceFirst(".*?\\.", "");
        }

        qualifiedName = kind2Node.getName() + "." + name;
        line = jsonObject.get(Kind2Labels.line).getAsLong();
        column = jsonObject.get(Kind2Labels.line).getAsLong();
    }

    private Kind2Analysis getKind2Analysis()
    {
        return this.kind2Node.getModelElementSet().getPostAnalysis().getAnalysis();
    }

    public String getJson()
    {
        return json;
    }

    public JsonElement getJsonElement()
    {
        return jsonElement;
    }

    public String getCategory()
    {
        return category;
    }

    public String getJsonName()
    {
        return jsonName;
    }

    public long getLine()
    {
        return line;
    }

    public long getColumn()
    {
        return column;
    }

    public Kind2Node getKind2Node()
    {
        return kind2Node;
    }

    public Kind2Property getKind2Property()
    {
        return kind2Property;
    }

    public String getName()
    {
        return name;
    }

    public String getQualifiedName()
    {
        return qualifiedName;
    }
}