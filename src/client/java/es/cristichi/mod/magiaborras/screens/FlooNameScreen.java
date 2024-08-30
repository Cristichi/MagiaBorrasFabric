package es.cristichi.mod.magiaborras.screens;

import es.cristichi.mod.magiaborras.floo.fireplace.FlooFireplaceBlockE;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;

// Well, this turned out to be work done for nothing. Keeping it jic the actual way to do this is similar.
public class FlooNameScreen extends Screen {
    FlooFireplaceBlockE fireplace;
    public FlooNameScreen(FlooFireplaceBlockE fireplace) {
        super(Text.translatable("magiaborras.screen.floonet.title"));
        this.fireplace = fireplace;
    }

    public TextWidget txtTitle;
    public TextFieldWidget inTxtName;
    public ButtonWidget btnRegister, btnUnregister;

    @Override
    public void close() {
        super.close();
        String text = inTxtName.getText();
        fireplace.setName(text);
    }

    @Override
    protected void init() {
        assert client != null;
        assert client.player != null;

        int xMargin = width/5;
        int yMargins = 10;

        int fullWidth = width*3/5;
        int standardHeigh = 20;

        txtTitle = new TextWidget(xMargin, yMargins, width*4/5, standardHeigh,
                Text.translatable("magiaborras.screen.floonet.title"), textRenderer);
        addDrawableChild(txtTitle);

        inTxtName = new TextFieldWidget(textRenderer, xMargin, standardHeigh+yMargins*2, fullWidth, standardHeigh,
                null, Text.of(fireplace.getName()));
        addDrawableChild(inTxtName);

        btnRegister = ButtonWidget.builder(Text.translatable("magiaborras.screen.flooname.register"),
                        button -> {
                            close();
                        })
                .dimensions(xMargin, standardHeigh*2+yMargins*3, fullWidth, standardHeigh)
                .tooltip(Tooltip.of(Text.translatable("magiaborras.screen.flooname.register.tooltip")))
                .build();

        addDrawableChild(btnRegister);

        btnUnregister = ButtonWidget.builder(Text.translatable("magiaborras.screen.flooname.unregister"),
                        button -> {
                            close();
                        })
                .dimensions(xMargin, standardHeigh*3+yMargins*4, fullWidth, standardHeigh)
                .tooltip(Tooltip.of(Text.translatable("magiaborras.screen.flooname.register.tooltip")))
                .build();

        addDrawableChild(btnUnregister);
    }
}
