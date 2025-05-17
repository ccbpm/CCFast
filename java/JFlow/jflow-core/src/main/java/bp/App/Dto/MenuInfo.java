package bp.App.Dto;

import java.util.List;

public class MenuInfo {
    private String name;
    private String path;
    private Boolean hidden;

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

//    public String getRedirect() {
//        return redirect;
//    }
//
//    public void setRedirect(String redirect) {
//        this.redirect = redirect;
//    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

//    public Boolean getAlwaysShow() {
//        return alwaysShow;
//    }
//
//    public void setAlwaysShow(Boolean alwaysShow) {
//        this.alwaysShow = alwaysShow;
//    }

    public MetaInfo getMeta() {
        return meta;
    }

    public void setMeta(MetaInfo meta) {
        this.meta = meta;
    }

    public List<MenuCh> getChildren() {
        return children;
    }

    public void setChildren(List<MenuCh> children) {
        this.children = children;
    }

    //private String redirect;
    private String component;
    //private Boolean alwaysShow;
    private MetaInfo meta;
    private List<MenuCh> children;
}
