package Modelos;

public class Reporte {
    private String asunto;
    private String descripcion;
    private String image;
    public Reporte(String asunto,String descripcion,String image){
        asunto=asunto;
        descripcion=descripcion;
        image=image;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getAsunto() {
        return asunto;
    }

    public String getImage() {
        return image;
    }
}
