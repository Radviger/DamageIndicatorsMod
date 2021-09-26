package ru.radviger.damageindicators.textures;

public enum EnumSkinPart {
    FRAMENAME(null, "/DITextures/Default/DIFramSkin.png"),
    FRAMEID(null, null),
    TYPEICONSNAME(null, "/DITextures/Default/DITypeIcons.png"),
    TYPEICONSID(null, null),
    DAMAGENAME(null, "/DITextures/Default/damage.png"),
    DAMAGEID(null, null),
    HEALTHNAME(null, "/DITextures/Default/health.png"),
    HEALTHID(null, null),
    BACKGROUNDNAME(null, "/DITextures/Default/background.png"),
    BACKGROUNDID(null, null),
    NAMEPLATENAME(null, "/DITextures/Default/NamePlate.png"),
    NAMEPLATEID(null, null),
    LEFTPOTIONNAME(null, "/DITextures/Default/leftPotions.png"),
    LEFTPOTIONID(null, null),
    RIGHTPOTIONNAME(null, "/DITextures/Default/rightPotions.png"),
    RIGHTPOTIONID(null, null),
    CENTERPOTIONNAME(null, "/DITextures/Default/centerPotions.png"),
    CENTERPOTIONID(null, null),
    ORDERING(null, Ordering.values()),
    CONFIGHEALTHBARHEIGHT("HealthBarHeight", 17),
    CONFIGHEALTHBARWIDTH("HealthBarWidth", 112),
    CONFIGHEALTHBARX("HealthBarXOffset", 49),
    CONFIGHEALTHBARY("HealthBarYOffset", 13),
    CONFIGFRAMEHEIGHT("FrameHeight", 64),
    CONFIGFRAMEWIDTH("FrameWidth", 178),
    CONFIGFRAMEX("FrameXOffset", -15),
    CONFIGFRAMEY("FrameYOffset", -5),
    CONFIGBACKGROUNDHEIGHT("BackgroundHeight", 51),
    CONFIGBACKGROUNDWIDTH("BackgroundWidth", 49),
    CONFIGBACKGROUNDX("BackgroundXOffset", -4),
    CONFIGBACKGROUNDY("BackgroundYOffset", -4),
    CONFIGNAMEPLATEHEIGHT("NamePlateHeight", 12),
    CONFIGNAMEPLATEWIDTH("NamePlateWidth", 112),
    CONFIGNAMEPLATEX("NamePlateXOffset", 49),
    CONFIGNAMEPLATEY("NamePlateYOffset", 0),
    CONFIGMOBTYPEHEIGHT("MobTypeSizeHeight", 18),
    CONFIGMOBTYPEWIDTH("MobTypeSizeWidth", 18),
    CONFIGMOBTYPEX("MobTypeOffsetX", -13),
    CONFIGMOBTYPEY("MobTypeOffsetY", 39),
    CONFIGPOTIONBOXHEIGHT("PotionBoxHeight", 22),
    CONFIGPOTIONBOXWIDTH("PotionBoxSidesWidth", 4),
    CONFIGPOTIONBOXX("PotionBoxOffsetX", 48),
    CONFIGPOTIONBOXY("PotionBoxOffsetY", 31),
    CONFIGMOBPREVIEWX("MobPreviewOffsetX", -4),
    CONFIGMOBPREVIEWY("MobPreviewOffsetY", -3),
    CONFIGTEXTEXTNAMECOLOR("NameTextColor", "FFFFFF"),
    CONFIGTEXTEXTHEALTHCOLOR("HealthTextColor", "FFFFFF"),
    CONFIGDISPLAYNM("SkinName", "Clean"),
    CONFIGAUTHOR("Author", "rich1051414"),
    INTERNAL(null, "/DITextures/Default/");

    private final Object ext;
    private final Object extDefault;

    EnumSkinPart(Object extended, Object configDefault) {
        this.ext = extended;
        this.extDefault = configDefault;
    }

    public final Object getExtended() {
        return this.ext;
    }

    public final Object getConfigDefault() {
        return this.extDefault;
    }
}
