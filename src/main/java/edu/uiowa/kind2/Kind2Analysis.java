/*
 * Copyright (C) 2019  The University of Iowa
 * @author Mudathir Mohamed
 */

package edu.uiowa.kind2;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Kind2Analysis
{
    private final String json;
    private final String nodeName;

    private final List<String> abstractNodes;
    private final List<String> concreteNodes;
    private final List<Pair<String, String>> assumptions;
    private final List<String> subNodes;

    private final Map<String, List<Kind2Property>> propertiesMap;

    private boolean isModeAnalysis = false;

    private Kind2NodeResult nodeResult = null;

    public Kind2Analysis(JsonElement jsonElement)
    {
        json = new GsonBuilder().setPrettyPrinting().create().toJson(jsonElement);

        this.nodeName = jsonElement.getAsJsonObject().get(Kind2Labels.top).getAsString();
        this.abstractNodes = new ArrayList<>();
        JsonArray abstractArray = jsonElement.getAsJsonObject().get(Kind2Labels.abstractField).getAsJsonArray();
        for (JsonElement node : abstractArray)
        {
            abstractNodes.add(node.getAsString());
        }
        this.concreteNodes = new ArrayList<>();
        JsonArray concreteArray = jsonElement.getAsJsonObject().get(Kind2Labels.concrete).getAsJsonArray();
        for (JsonElement node : concreteArray)
        {
            concreteNodes.add(node.getAsString());
        }

        subNodes = new ArrayList<>(abstractNodes);
        subNodes.addAll(concreteNodes);

        assumptions = new ArrayList<>();

        JsonArray assumptionInvariants = jsonElement.getAsJsonObject().get(Kind2Labels.assumptions).getAsJsonArray();
        for (JsonElement invariant : assumptionInvariants)
        {
            JsonArray invariantArray = invariant.getAsJsonArray();
            String nodeName = invariantArray.get(0).getAsString();
            String number = invariantArray.get(1).getAsString();
            assumptions.add(new Pair<>(nodeName, number));
            if (!subNodes.contains(nodeName) && !nodeName.equals(this.nodeName))
            {
                //ToDo: find a permanent solution to invariants.
                // subNodes.add(nodeName);
            }
        }

        this.propertiesMap = new HashMap<>();
    }

    public void addProperty(Kind2Property property)
    {
        // add the property
        if (propertiesMap.containsKey(property.getJsonName()))
        {
            propertiesMap.get(property.getJsonName()).add(property);
        }
        else
        {
            List<Kind2Property> list = new ArrayList<>();
            list.add(property);
            propertiesMap.put(property.getJsonName(), list);
        }
        if (property.getSource() == Kind2PropertyType.oneModeActive)
        {
            isModeAnalysis = true;
        }
    }

    public String getJson()
    {
        return json;
    }

    public String getNodeName()
    {
        return nodeName;
    }

    public String getNodeMappedName()
    {
        if (nodeResult == null)
        {
            return nodeName;
        }
        return nodeResult.getOriginalName();
    }

    public List<String> getAbstractNodes()
    {
        return abstractNodes;
    }

    public List<String> getConcreteNodes()
    {
        return concreteNodes;
    }

    public List<Kind2Property> getProperties()
    {
        List<Kind2Property> lastProperties = new ArrayList<>();
        for (Map.Entry<String, List<Kind2Property>> entry : propertiesMap.entrySet())
        {
            // add the last property object
            Kind2Property lastProperty = entry.getValue().get(entry.getValue().size() - 1);
            lastProperties.add(lastProperty);
        }
        return lastProperties;
    }

    private List<Kind2Property> filterProperties(Kind2Answer answer)
    {
        return getProperties().stream().filter(p -> p.getAnswer() == answer).collect(Collectors.toList());
    }

    public List<Kind2Property> getFalsifiedProperties()
    {
        return filterProperties(Kind2Answer.falsifiable);
    }

    public List<Kind2Property> getUnknownProperties()
    {
        return filterProperties(Kind2Answer.unknown);
    }

    public List<String> getSubNodes()
    {
        return subNodes;
    }

    public boolean isModeAnalysis()
    {
        return isModeAnalysis;
    }

    public void setNodeResult(Kind2NodeResult nodeResult)
    {
        this.nodeResult = nodeResult;
    }

    public Kind2NodeResult getNodeResult()
    {
        return nodeResult;
    }

    public Kind2Result getKind2Result()
    {
        if (nodeResult == null)
        {
            return null;
        }
        else
        {
            return nodeResult.getKind2Result();
        }
    }

    public List<Kind2Property> getValidProperties()
    {
        return filterProperties(Kind2Answer.valid);
    }

    public Map<String, List<Kind2Property>> getPropertiesMap()
    {
        return propertiesMap;
    }
}
