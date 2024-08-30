package es.cristichi.mod.magiaborras.screens;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;

// Well, this turned out to be work done for nothing. Keeping it jic the actual way to do this is similar.
public class FlooNameScreen extends Screen {
    private String name;
    private boolean registered;

    public FlooNameScreen(String currentName, boolean registered) {
        super(Text.translatable("magiaborras.screen.flooname.title"));
        this.name = currentName;
        this.registered = registered;
    }

    public String getName() {
        return name;
    }

    public boolean getRegistered() {
        return registered;
    }

    public TextWidget txtTitle;
    public TextFieldWidget inTxtName;
    public ButtonWidget btnRegister, btnUnregister;

    @Override
    protected void init() {
        assert client != null;
        assert client.player != null;

        int xMargin = width/5;
        int yMargins = 10;

        int fullWidth = width*3/5;
        int standardHeigh = 20;

        txtTitle = new TextWidget(0, yMargins, width, standardHeigh,
                Text.translatable("magiaborras.screen.flooname.title"), textRenderer);
        txtTitle.alignCenter();
        txtTitle.setMessage(Text.translatable("magiaborras.screen.flooname.title"));
        addDrawableChild(txtTitle);

        inTxtName = new TextFieldWidget(textRenderer, xMargin, standardHeigh+yMargins*2, fullWidth, standardHeigh,
                null, Text.of(name));
        inTxtName.setMaxLength(100);
        inTxtName.setText(name);
        inTxtName.setCursorToStart(false);
        addDrawableChild(inTxtName);

        btnRegister = ButtonWidget.builder(Text.translatable("magiaborras.screen.flooname.register"),
                        button -> {
                            registered = true;
                            name = inTxtName.getText();
                            close();
                        })
                .dimensions(xMargin, standardHeigh*2+yMargins*3, fullWidth, standardHeigh)
                .tooltip(Tooltip.of(Text.translatable("magiaborras.screen.flooname.register.tooltip")))
                .build();

        addDrawableChild(btnRegister);

        btnUnregister = ButtonWidget.builder(Text.translatable("magiaborras.screen.flooname.unregister"),
                        button -> {
                            registered = false;
                            name = inTxtName.getText();
                            close();
                        })
                .dimensions(xMargin, standardHeigh*3+yMargins*4, fullWidth, standardHeigh)
                .tooltip(Tooltip.of(Text.translatable("magiaborras.screen.flooname.register.tooltip")))
                .build();

        addDrawableChild(btnUnregister);
    }
}
