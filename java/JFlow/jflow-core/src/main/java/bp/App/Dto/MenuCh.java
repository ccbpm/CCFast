package bp.App.Dto;

import java.util.List;

public class MenuCh {
    private String name;
    private String path;
    private Boolean hidden;
    private String component;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public MetaInfo getMeta() {
        return meta;
    }

    public void setMeta(MetaInfo meta) {
        this.meta = meta;
    }

    private MetaInfo meta;

    private List<MenuCh> children;
    public List<MenuCh> getChildren() {
        return children;
    }

    public void setChildren(List<MenuCh> children) {
        this.children = children;
    }
}
