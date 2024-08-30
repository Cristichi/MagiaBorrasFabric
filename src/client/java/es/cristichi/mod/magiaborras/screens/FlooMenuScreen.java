package es.cristichi.mod.magiaborras.screens;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// Well, this turned out to be work done for nothing. Keeping it jic the actual way to do this is similar.
public class FlooMenuScreen extends Screen {
    private final HashMap<BlockPos, String> fireplaces;
    private BlockPos selected = null;

    public FlooMenuScreen(HashMap<BlockPos, String> fireplaces) {
        super(Text.translatable("magiaborras.screen.flooname.title"));
        this.fireplaces = fireplaces;
    }

    @Nullable
    public BlockPos getSelected() {
        return selected;
    }

    public TextWidget txtTitle, txtSubtitle;
    public ArrayList<ButtonWidget> btns;

    @Override
    protected void init() {
        assert client != null;
        assert client.player != null;

        int xMargin = width/11;
        int xMarginRight = width*6/11;
        int yMargins = 1;

        int halfWidth = width*4/11;
        int standardHeigh = 18;

        txtTitle = new TextWidget(0, yMargins, width, standardHeigh,
                Text.translatable("magiaborras.screen.floonet.title"), textRenderer);
        txtTitle.alignCenter();
        txtTitle.setMessage(Text.translatable("magiaborras.screen.floonet.title"));
        addDrawableChild(txtTitle);

        txtSubtitle = new TextWidget(0, standardHeigh+yMargins*2, width, standardHeigh,
                Text.translatable("magiaborras.screen.floonet.subtitle"), textRenderer);
        txtSubtitle.alignCenter();
        txtSubtitle.setMessage(Text.translatable("magiaborras.screen.floonet.subtitle"));
        addDrawableChild(txtSubtitle);

        btns = new ArrayList<>(fireplaces.size());
        int cont = 2;
        for (Map.Entry<BlockPos, String> entry : fireplaces.entrySet()) {
            boolean left = cont%2==0;
            BlockPos key = entry.getKey();
            String value = entry.getValue();

            ButtonWidget btn  = ButtonWidget.builder(Text.translatable("magiaborras.screen.floonet.btn",
                                    value, key.getX(), key.getY(), key.getZ()),
                        button -> {
                            selected = key;
                            close();
                        })
                .dimensions(left?xMargin:xMarginRight, left?standardHeigh*cont+yMargins*(cont+1):standardHeigh*(cont-1)+yMargins*(cont),
                        halfWidth, standardHeigh)
                .build();

            btns.add(btn);
            addDrawableChild(btn);
            cont++;
        }

    }


}
