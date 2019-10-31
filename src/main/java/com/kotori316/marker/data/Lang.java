package com.kotori316.marker.data;

import javax.annotation.Nonnull;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

import com.kotori316.marker.Marker;

abstract class Lang extends LanguageProvider {
    public Lang(DataGenerator gen, String locale) {
        super(gen, Marker.modID, locale);
    }

    @Override
    protected void addTranslations() {
        LangData data = getLangData();
        addBlock(() -> Marker.blockMarker, data.marker);
        addBlock(() -> Marker.block16Marker, data.marker16);
        addGuiEnrty("left", data.left);
        addGuiEnrty("right", data.right);
        addGuiEnrty("forward", data.forward);
    }

    protected void addGuiEnrty(String key, String localized) {
        add("gui." + key, localized);
    }

    @Nonnull
    protected abstract LangData getLangData();

    private static class LangData {
        final String marker;
        final String marker16;
        final String left, right, forward;

        private LangData(String marker, String marker16, String left, String right, String forward) {
            this.marker = marker;
            this.marker16 = marker16;
            this.left = left;
            this.right = right;
            this.forward = forward;
        }
    }

    public static class LangUS extends Lang {

        public LangUS(DataGenerator gen) {
            super(gen, "en_us");
        }

        @Override
        protected LangData getLangData() {
            return new LangData(
                "Flexible Marker",
                "Chunk Marker",
                "Left",
                "Right",
                "Forward"
            );
        }
    }

    public static class LangJP extends Lang {

        public LangJP(DataGenerator gen) {
            super(gen, "ja_jp");
        }

        @Nonnull
        @Override
        protected LangData getLangData() {
            return new LangData(
                "Flexibleなマーカー",
                "Chunkっぽいマーカー",
                "左",
                "右",
                "前方"
            );
        }
    }
}
