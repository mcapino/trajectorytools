package tt.vis;

public class LineHUDTextProvider implements LineHUDLayer.TextProvider {

    private String text;

    public LineHUDTextProvider(String text) {
        this.text = text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }
}
