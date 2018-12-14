package it.nextworks.composer.plugins.catalogue;

public abstract class CataloguePlugin implements CatalogueProviderInterface {


    CatalogueType type;
    Catalogue catalogue;

    public CataloguePlugin(CatalogueType type, Catalogue catalogue) {
        this.type = type;
        this.catalogue = catalogue;
    }

    public CatalogueType getType() {
        return type;
    }

    public void setType(CatalogueType type) {
        this.type = type;
    }

    public Catalogue getCatalogue() {
        return catalogue;
    }

    public void setCatalogue(Catalogue catalogue) {
        this.catalogue = catalogue;
    }


}
