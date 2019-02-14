package com.superbigbang.mushelp;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.google.android.gms.ads.MobileAds;
import com.superbigbang.mushelp.di.BaseComponent;
import com.superbigbang.mushelp.di.DaggerBaseComponent;
import com.superbigbang.mushelp.di.DaggerMetroComponent;
import com.superbigbang.mushelp.di.MetroComponent;
import com.superbigbang.mushelp.di.modules.ContextModule;
import com.superbigbang.mushelp.di.modules.MetronomeServiceModule;
import com.superbigbang.mushelp.model.SetList;
import com.superbigbang.mushelp.model.Songs;
import com.superbigbang.mushelp.services.MetronomeService;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import timber.log.Timber;


public class ExtendApplication extends Application implements ServiceConnection {

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_FIRST_INSTALL_FLAG = "FIRST_INSTALL_FLAG";
    public static boolean isBound;
    private static BaseComponent sBaseComponent;
    private static MetroComponent sMetroComponent;
    public static int currentThemeColorsTextSelected = Color.parseColor("#FFFFFFFF");

    //==================Lyrics added for Test:
    private String Amon_Amarth_One_Against_All_lyrics = "Winter's lost its grip\n" +
            "The ocean is set free\n" +
            "The ship glides through the broken ice\n" +
            "Out to an open sea\n" +
            "North winds fill the sails\n" +
            "They fly on frothing seas\n" +
            "As hope grows stronger in his heart\n" +
            "It's easier to breathe\n" +
            "Days turn into nights\n" +
            "Nights turn into days\n" +
            "His determination grows\n" +
            "With every breath he takes\n" +
            "There he stands alone, one man against all\n" +
            "With a sword in each hand, soon he will fall\n" +
            "There he stands alone, one man against all\n" +
            "With a sword in each hand, heeding the call\n" +
            "When the reach the Hanö bay\n" +
            "There waits a ship of war\n" +
            "Like the bear attacks its prey\n" +
            "It comes at them with force\n" +
            "All men to the oars\n" +
            "Row for all your worth\n" +
            "Most likely this will be your last day\n" +
            "On this wretched earth\n" +
            "The weak they try to run\n" +
            "But he's prepared to fight\n" +
            "One by one his friends are slain\n" +
            "Only he remains\n" +
            "He knows the end is near\n" +
            "They have him in their jaws\n" +
            "When a noble man appears\n" +
            "He tells them, withdraw\n" +
            "There he stands before him\n" +
            "As the skirmish quells\n" +
            "He offers him\n" +
            "Join our crew or join your friends in hell\n" +
            "There he stands alone, one man against all\n" +
            "With a sword in each hand, and soon he will fall\n" +
            "There he stands alone, one man against all\n" +
            "With a sword in each hand, he's heeding the call\n" +
            "Авторы: Ted Oscar Lundstroem / Olavi Mikkonen / Johan Soederberg / Johan Hegg\n" +
            "Текст песни \"One Against All\", © Sony/ATV Music Publishing LLC";
    private String Amon_Amarth_We_Shall_Destroy_lyrics = "Sword and spear\n" +
            "Wall of shields\n" +
            "Standing strong\n" +
            "On this their chosen battle field\n" +
            "Form the lines\n" +
            "Shield by shield\n" +
            "Side by side\n" +
            "We're marching into destiny\n" +
            "March as one\n" +
            "Don't look back\n" +
            "Oden's sons\n" +
            "Attack!\n" +
            "Unleash hell! Do not repent!\n" +
            "Warfare grants us no lament\n" +
            "Let your weapons slash and tear\n" +
            "This is no place for fear\n" +
            "Hold the lines! Move as one!\n" +
            "In unity our victory's won\n" +
            "Our shields will form a mighty wall\n" +
            "United we shall never fall\n" +
            "Fear will not pierce our hearts\n" +
            "Though swords and arrows will\n" +
            "Victory is our reward\n" +
            "For all the blood we spill\n" +
            "We didn't come to waste our lives\n" +
            "Like pawns in savage game\n" +
            "Our spirits, spears and shields are linked\n" +
            "In a much stronger chain\n" +
            "March as one\n" +
            "Don't look back\n" +
            "Oden's sons\n" +
            "Attack!\n" +
            "All are one\n" +
            "And one is all\n" +
            "Side by side\n" +
            "The battle calls\n" +
            "All are one\n" +
            "And one is all\n" +
            "Shield by shield\n" +
            "We'll never fall\n" +
            "All are one\n" +
            "And one is all\n" +
            "Side by side\n" +
            "The battle calls\n" +
            "All are one\n" +
            "And one is all\n" +
            "Shield by shield\n" +
            "We'll never fall\n" +
            "Unleash hell! Do not repent!\n" +
            "Warfare grants us no lament\n" +
            "Let your weapons slash and tear\n" +
            "This is no place for fear\n" +
            "Charge with force! Break their ranks!\n" +
            "No remorse, crush their flanks!\n" +
            "Pulverize their human wall!\n" +
            "We shall destroy!\n" +
            "Авторы: Ted Oscar Lundstroem / Olavi Mikkonen / Johan Soederberg / Fredrik Andersson / Johan Hegg\n" +
            "Текст песни \"We Shall Destroy\", © Sony/ATV Music Publishing LLC";
    private String Amon_Amarth_Cry_of_blackbirds_lyrics = "Raise your swords up high!\n" +
            "See the black birds fly!\n" +
            "Let them hear your rage!\n" +
            "Show no fear!\n" +
            "Attack!\n" +
            "Charge your horses across the fields\n" +
            "Together we ride into destiny\n" +
            "Have no fear of death, when its our time\n" +
            "Oden will bring us home, when we die!\n" +
            "The ground trembles under us\n" +
            "As we make our thunder charge\n" +
            "The pounding hooves spread panic and fear into their hearts\n" +
            "Our helmets shine in the sun as we near their wall of shields\n" +
            "Some of them turn and run\n" +
            "When they hear our frenzied screams!\n" +
            "Draw your swords to strike!\n" +
            "Hear the Black Birds cry!\n" +
            "Let them feel your hate!\n" +
            "Show no fear!\n" +
            "Charge your horses across the fields\n" +
            "Together we ride into destiny\n" +
            "Have no fear of death, when its our time\n" +
            "Oden will bring us home, when we die!\n" +
            "The enemy are in disarray ride them down as they run\n" +
            "Send them to their violent graves don't spare anyone\n" +
            "Dead and wounded lie all around see the pain in their eyes\n" +
            "Over the field an eerie sound, as we hear the ravens cry\n" +
            "Авторы: Fredrick Andersson / Johan Hans Hegg / Johan Olof Soderberg / Olavi Petteri Mikkonen / Ted Lundstrom\n" +
            "Текст песни \"Cry of the Black Birds\", © BMG Rights Management";
    private String Amon_Amarth_At_downs_First_light_lyrics = "At dawn's first light\n" +
            "Run for your lives\n" +
            "Viking ships come out of the mist\n" +
            "Ominous sight\n" +
            "Flee or stand fast\n" +
            "You won't last\n" +
            "Warlords invade with axe and blade\n" +
            "Attack at first light\n" +
            "Unstoppable force\n" +
            "No remorse\n" +
            "Under attack, no turning back\n" +
            "You cannot run\n" +
            "Nowhere to hide\n" +
            "No shelter inside\n" +
            "Under attack, no turning back\n" +
            "Slaughter's begun\n" +
            "Nowhere to hid\n" +
            "No shelter inside\n" +
            "Slashing, killing\n" +
            "Thrashing, spilling\n" +
            "Blood for honor, death, and glory\n" +
            "Out of the night\n" +
            "At dawn's first light\n" +
            "Longships arrive\n" +
            "Run for your lives\n" +
            "Wounds won't mend your bitter end\n" +
            "Death to all\n" +
            "Flee or fight\n" +
            "No one survives\n" +
            "Bloodshed won't stop\n" +
            "'Til the last drop\n" +
            "You will fall\n" +
            "It matters not how hard you fought\n" +
            "Under attack, no turning back\n" +
            "You cannot run\n" +
            "Nowhere to hide\n" +
            "No shelter inside\n" +
            "Under attack, no turning back\n" +
            "Slaughter's begun\n" +
            "Nowhere to hid\n" +
            "No shelter inside\n" +
            "Slashing, killing\n" +
            "Thrashing, spilling\n" +
            "Blood for honor, death, and glory\n" +
            "The ships bring terror to these shores\n" +
            "Death and all out war\n" +
            "Their oars are cutting through the waves\n" +
            "Like spades are digging graves\n" +
            "At dawn's first light\n" +
            "Run for your lives\n" +
            "Out of the night\n" +
            "At dawn's first light\n" +
            "Longships arrive\n" +
            "Run for your lives\n" +
            "Авторы: Ted Oscar Lundstroem / Olavi Mikkonen / Johan Soederberg / Johan Hegg\n" +
            "Текст песни \"At Dawn's First Light\", © Sony/ATV Music Publishing LLC";
    private String Amon_Amarth_First_Kill_lyrics = "The first man I killed was the earl's right-hand man\n" +
            "When he came to take her away\n" +
            "I ran his own sword straight through his throat\n" +
            "And then I stood there, watching him fall\n" +
            "The first blood I spilled was the blood of a bard\n" +
            "I had to wipe the smile away\n" +
            "I was not yet a man, nor was I a boy\n" +
            "But still, I made that bastard pay\n" +
            "So I left him there, on the stone floor\n" +
            "Bathing in a pool of his own blood\n" +
            "My one and only choice was to flee this land\n" +
            "To leave this wretched place for good\n" +
            "I am an outcast\n" +
            "All alone\n" +
            "I'm a nomad without home\n" +
            "I am an outlaw\n" +
            "I'm disowned\n" +
            "And I am no man's son\n" +
            "Through the cold midwinter nights on a southbound winding path\n" +
            "The stars and moon my only light and the earl's men closing fast\n" +
            "I swore that I'd return that I would see him burn\n" +
            "I will live it in my dreams the smell, the blood, his dying screams\n" +
            "To my father I was dead, he took his hand from me\n" +
            "He drove me away, I was shunned\n" +
            "My one and only choice was to leave this land\n" +
            "To become the pagan they would hunt\n" +
            "I am an outcast\n" +
            "All alone\n" +
            "I'm a nomad without home\n" +
            "I am an outlaw\n" +
            "I'm disowned\n" +
            "And I am no man's son\n" +
            "I am an outcast\n" +
            "All alone\n" +
            "I'm a nomad without home\n" +
            "I am an outlaw\n" +
            "I'm disowned\n" +
            "And I am no man's son\n" +
            "Авторы: Ted Oscar Lundstroem / Olavi Mikkonen / Johan Soederberg / Johan Hegg\n" +
            "Текст песни \"First Kill\", © Sony/ATV Music Publishing LLC";
    private String Arch_Enemy_Eagles_Flys_alone = "When I was born the seed was sown\n" +
            "I will not obey, my life is my own\n" +
            "Battle rows, which do enslave me\n" +
            "Exposed lies that enrage me\n" +
            "I don't believe in heaven, I don't believe in hell\n" +
            "Never joined the herd, could not adjust well\n" +
            "Slave and master, it's not for me\n" +
            "I choose my own path, set myself free\n" +
            "I, I go my own way\n" +
            "I swim against the stream\n" +
            "Forever I will fight the powers that be\n" +
            "I, I go my own way\n" +
            "I swim against the stream\n" +
            "Forever I will fight the pοwers that be\n" +
            "The eagle flies alone\n" +
            "Reject the system that dictates the norm\n" +
            "This world is full of lies and deceit\n" +
            "I ask my own betrayal, cut so deep\n" +
            "Suffered defeat only to rise again\n" +
            "I, I go my own way\n" +
            "I swim against the stream\n" +
            "Forever I will fight the powers that be\n" +
            "I, I go my own way\n" +
            "I swim against the stream\n" +
            "Forever I will fight the powers that be\n" +
            "The eagle flies alone\n" +
            "Alone!\n" +
            "I, I go my own way\n" +
            "I swim against the stream\n" +
            "Forever I will fight the powers that be\n" +
            "I, I go my own way\n" +
            "I swim against the stream\n" +
            "Forever I will fight the powers that be\n" +
            "The eagle flies alone\n" +
            "Авторы: Michael Amott\n" +
            "Текст песни \"The Eagle Flies Alone\", © Kobalt Music Publishing Ltd.";
    private String Arch_Enemy_Avalanche = "A fistful of fear in my hands\n" +
            "A bullet of betrayal in my brain\n" +
            "No progress comes from pleasure\n" +
            "We smile in ignorance and learn in pain\n" +
            "Willful deceit was your plan\n" +
            "Desperately avoid the blame\n" +
            "Who will you answer to now?\n" +
            "Sharpened your shovels\n" +
            "Just to dig your grave\n" +
            "Stand up because the ground is in your way\n" +
            "I won't give you any piece\n" +
            "Yet still you talk\n" +
            "Try to fight but I will watch you fall\n" +
            "Fall\n" +
            "This is sweet revenge\n" +
            "And karma's a bitch\n" +
            "You glutton for punishment\n" +
            "What did you expect?\n" +
            "Sick, sick, sick\n" +
            "I'm sick of being your martyr\n" +
            "Your inglated ego is just dead weight\n" +
            "Bask in all the eyes upon you\n" +
            "Before you know it\n" +
            "The'll have turned away\n" +
            "Stand up as the whole world turns away\n" +
            "I won't give you any peace\n" +
            "Yet still you talk\n" +
            "Try to fight but I will watch you fall\n" +
            "Fall\n" +
            "This is sweet revenge\n" +
            "And karma's a bitch\n" +
            "You glutton for punishment\n" +
            "What did you expect?\n" +
            "Wait and see\n" +
            "Cry yourself a lonesome creek\n" +
            "Your decaying corpse\n" +
            "Can feed the roots\n" +
            "Of my towering free\n" +
            "Just wait and see\n" +
            "You're dead to me\n" +
            "Yet still you speak\n" +
            "Trying to justify what you're doing to me\n" +
            "I will watch you fall\n" +
            "Fall\n" +
            "This is sweet revenge\n" +
            "And karma's a bitch\n" +
            "You glutton for punishment\n" +
            "What did you\n" +
            "What did you expect?\n" +
            "Авторы: Alissa White-Gluz / Daniel Erlandsson / Michael Amott / Nicholas Cordle\n" +
            "Текст песни \"Avalanche\", © Kobalt Music Publishing Ltd.";
    private String Arch_Enemy_Ravenous = "I am hunting for your soul\n" +
            "It dwells within your heart\n" +
            "I lacerate your pounding flesh\n" +
            "Your spirits shall be mine\n" +
            "So rise my spirits rise\n" +
            "Revel in this dead man's body\n" +
            "Grip his soul, sip the blood\n" +
            "Life in death\n" +
            "A holy carnage\n" +
            "Ravenous\n" +
            "I will be a god\n" +
            "Carnivorous Jesus\n" +
            "I need your flesh\n" +
            "Ritual slaughter\n" +
            "Fill up the chalice\n" +
            "With the essence of your life\n" +
            "Liquid strengths trapped in your veins\n" +
            "I crave your blood\n" +
            "You must die\n" +
            "Ravenous\n" +
            "I will be a god\n" +
            "Carnivorous Jesus\n" +
            "I need your flesh\n" +
            "Ravenous\n" +
            "I will be a god\n" +
            "Carnivorous Jesus\n" +
            "I need your flesh\n" +
            "Flesh\n" +
            "Авторы: Marc Edwin Allen / Nathan Barcalow / Derek Doherty / Alejandro Martinez Linares / Randy Strohmeyer\n" +
            "Текст песни \"Ravenous\", © Kobalt Music Publishing Ltd., Universal Music Publishing Group";
    private String Arch_Enemy_The_world_is_Yours = "Rise into the light and fade to the night\n" +
            "Sick of being told how to run your life\n" +
            "Their rules\n" +
            "They're fools\n" +
            "Empty words they promise so much\n" +
            "The present status quo remains untouched\n" +
            "If you want the world\n" +
            "Use your mind\n" +
            "Take control\n" +
            "Feel the strength\n" +
            "Rise from within\n" +
            "If you really want it the world is yours\n" +
            "Every empire was raised by the slain\n" +
            "Built through the age and you can destroy it in a day\n" +
            "Turn the page\n" +
            "Unleash your rage\n" +
            "Burn your golden cage and walk away\n" +
            "On your path toward ultimate power\n" +
            "If you want the world\n" +
            "Use your mind\n" +
            "Take control\n" +
            "Feel the strength\n" +
            "Rise from within\n" +
            "If you really want it the world is yours\n" +
            "Striking at the leash\n" +
            "Foaming at the mouth\n" +
            "No more subservience\n" +
            "Justice will be done\n" +
            "There was only so much\n" +
            "You could take\n" +
            "There was only so much\n" +
            "You could tolerate\n" +
            "When the bough breaks, the empire will fall\n" +
            "If you want the world\n" +
            "Use your mind\n" +
            "Take control\n" +
            "Feel the strength\n" +
            "Rise from within\n" +
            "If you really want it the world is yours\n" +
            "If you want the world\n" +
            "Use your mind\n" +
            "Take control\n" +
            "Feel the strength\n" +
            "Rise from within\n" +
            "If you really want it the world is yours\n" +
            "Авторы: Daniel Erlandsson / Michael Amott\n" +
            "Текст песни \"The World Is Yours\", © Kobalt Music Publishing Ltd.";
    private String Arch_Enemy_Taking_back_my_soul = "Processed, diluted\n" +
            "Virtually unrecognizable\n" +
            "I was lost there, no direction\n" +
            "A scattered void\n" +
            "No more\n" +
            "It's over\n" +
            "I'm on to you\n" +
            "Your evil game\n" +
            "The tables have turned\n" +
            "I am taking back my soul\n" +
            "Tranquilized, scrutinized\n" +
            "Hate injected mind\n" +
            "As if internal wounds\n" +
            "Wouldn't bleed, wouldn't hurt me\n" +
            "I see through your lies\n" +
            "Taking back what's mine\n" +
            "Авторы: Angela Gossow / Christopher Amott / Daniel Erlandsson / Michael Amott\n" +
            "Текст песни \"Taking Back My Soul\", © Kobalt Music Publishing Ltd.";
    private String Arch_Enemy_As_the_pages_burn = "As the pages burn\n" +
            "Secrets can't be unlearned\n" +
            "Silence can't be unheard... Knowledge is burden\n" +
            "Power is onus as the pages burn\n" +
            "Rejecting cognition as the pages turn\n" +
            "Sinking in a pool of either\n" +
            "The past is washed clean\n" +
            "Rip apart the world\n" +
            "Word by word\n" +
            "Inch by inch\n" +
            "Sever your timeline\n" +
            "Silence your conscience\n" +
            "Sweet amnesia here to free you\n" +
            "As the pages burn (burn!)\n" +
            "All you trials solved bye fire\n" +
            "As the pages burn\n" +
            "As smoke fills the room soon to pass... heavy gloom\n" +
            "The wind will whisk away the past\n" +
            "A blank page stares at you\n" +
            "Nothing left\n" +
            "Where to start?\n" +
            "A second chance carries expectation!\n" +
            "The books you'd written no longer exist\n" +
            "The future is in your pen\n" +
            "Ink to paper now begin\n" +
            "Rip apart the world\n" +
            "Word by word\n" +
            "Inch by inch\n" +
            "Sever your timeline\n" +
            "Silence your conscience!\n" +
            "Sweet amnesia here to free you\n" +
            "As the pages burn (burn!)\n" +
            "All you trials solved bye fire\n" +
            "As the pages... burn!\n" +
            "Rewriting history\n" +
            "Starting now!\n" +
            "Erase the misery\n" +
            "Let the flames eat your doubts\n" +
            "Sweet amnesia here to free you\n" +
            "As the pages burn (burn!)\n" +
            "All you trials solved bye fire\n" +
            "As the pages burn!\n" +
            "Авторы: Alissa White-Gluz / Michael Amott\n" +
            "Текст песни \"As the Pages Burn\", © Kobalt Music Publishing Ltd.";

    public static BaseComponent getBaseComponent() {
        return sBaseComponent;
    }

    @VisibleForTesting
    public static void setBaseComponent(@NonNull BaseComponent baseComponent) {
        sBaseComponent = baseComponent;
    }

    public static MetroComponent getMetroComponent() {
        return sMetroComponent;
    }

    @VisibleForTesting
    public static void setMetroComponent(@NonNull MetroComponent metroComponent) {
        sMetroComponent = metroComponent;
    }

    public static boolean isBound() {
        return isBound;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        Intent intent = new Intent(this, MetronomeService.class);
        startService(intent);
        bindService(intent, this, Context.BIND_AUTO_CREATE);

        sBaseComponent = DaggerBaseComponent.builder()
                .contextModule(new ContextModule(this))
                .build();

        Realm.init(sBaseComponent.getContext());

        if (!mSettings.contains(APP_PREFERENCES_FIRST_INSTALL_FLAG)) {
            MyInitialDataRealmTransaction();
        }

        MobileAds.initialize(this, "ca-app-pub-5364969751338385~1161013636");

        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean(APP_PREFERENCES_FIRST_INSTALL_FLAG, false).apply();
    }

    @Override
    public void onTerminate() {
        if (isBound) {
            unbindService(this);
            isBound = false;
        }
        super.onTerminate();
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        MetronomeService.LocalBinder binder = (MetronomeService.LocalBinder) iBinder;
        MetronomeService service = binder.getService();
        isBound = true;
        sMetroComponent = DaggerMetroComponent.builder()
                .metronomeServiceModule(new MetronomeServiceModule(service))
                .build();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        isBound = false;
    }

    void MyInitialDataRealmTransaction() {
        RealmConfiguration setListsRealmConfig = new RealmConfiguration.Builder()
                .name("setlistsrealm.realm")
                .build();

        RealmConfiguration songsRealmConfig = new RealmConfiguration.Builder()
                .name("songsrealm.realm")
                .build();

        Realm setlistsrealm = Realm.getInstance(setListsRealmConfig);
        Realm songsrealm = Realm.getInstance(songsRealmConfig);

        setlistsrealm.beginTransaction();
        SetList setList1 = setlistsrealm.createObject(SetList.class); // Create a new object
        setList1.setId(0);
        setList1.setName("Hell Rain");
        setList1.setPosition(0);
        setList1.setOpen(true);
        SetList setList2 = setlistsrealm.createObject(SetList.class);
        setList2.setId(1);
        setList2.setName("Amon Amarth");
        setList2.setPosition(1);
        SetList setList3 = setlistsrealm.createObject(SetList.class);
        setList3.setId(2);
        setList3.setName("Arch Enemy");
        setList3.setPosition(2);
        for (int i = 3; i < 20; i++) {
            SetList setList = setlistsrealm.createObject(SetList.class);
            setList.setId(i);
            setList.setName(getResources().getString(R.string.SetListNameDefault) + (i + 1));
            setList.setPosition(i);
        }
        setlistsrealm.commitTransaction();
        setlistsrealm.close();

        songsrealm.beginTransaction();
        Songs song1 = songsrealm.createObject(Songs.class);
        song1.setTitle("Предатель");
        song1.setSetlistid(0);
        song1.setSongid(0);
        song1.setPosition(0);
        song1.setMetronombpm(145);
        song1.setAudioOn(false);
        song1.setLyrics("Текст песни Предатель");
        song1.setLyricshasopen(false);
        song1.setPlaystarted(false);

        Songs song2 = songsrealm.createObject(Songs.class);
        song2.setTitle("Проклятая жизнь");
        song2.setSetlistid(0);
        song2.setSongid(1);
        song2.setPosition(1);
        song2.setMetronombpm(115);
        song2.setAudioOn(false);
        song2.setLyrics("Текст песни Проклятая жизнь");
        song2.setLyricshasopen(false);
        song2.setPlaystarted(false);

        Songs song3 = songsrealm.createObject(Songs.class);
        song3.setTitle("Война");
        song3.setSetlistid(0);
        song3.setSongid(2);
        song3.setPosition(2);
        song3.setMetronombpm(130);
        song3.setAudioOn(false);
        song3.setLyrics("Текст песни Война");
        song3.setLyricshasopen(false);
        song3.setPlaystarted(false);

        Songs song4 = songsrealm.createObject(Songs.class);
        song4.setTitle("Мёртвый город");
        song4.setSetlistid(0);
        song4.setSongid(3);
        song4.setPosition(3);
        song4.setMetronombpm(170);
        song4.setAudioOn(false);
        song4.setLyrics("Текст песни Мёртвый город");
        song4.setLyricshasopen(false);
        song4.setPlaystarted(false);

        Songs song5 = songsrealm.createObject(Songs.class);
        song5.setTitle("Сон");
        song5.setSetlistid(0);
        song5.setSongid(4);
        song5.setPosition(4);
        song5.setMetronombpm(130);
        song5.setAudioOn(false);
        song5.setLyrics("Текст песни Сон");
        song5.setLyricshasopen(false);
        song5.setPlaystarted(false);

        Songs song6 = songsrealm.createObject(Songs.class);
        song6.setTitle("Клетка");
        song6.setSetlistid(0);
        song6.setSongid(5);
        song6.setPosition(5);
        song6.setMetronombpm(150);
        song6.setAudioOn(false);
        song6.setLyrics("Текст песни Клетка");
        song6.setLyricshasopen(false);
        song6.setPlaystarted(false);

        Songs song7 = songsrealm.createObject(Songs.class);
        song7.setTitle("Молчание вечного крика");
        song7.setSetlistid(0);
        song7.setSongid(6);
        song7.setPosition(6);
        song7.setMetronombpm(165);
        song7.setAudioOn(false);
        song7.setLyrics("Текст песни Молчание вечного крика");
        song7.setLyricshasopen(false);
        song7.setPlaystarted(false);

        Songs song8 = songsrealm.createObject(Songs.class);
        song8.setTitle(getResources().getString(R.string.Hint10));
        song8.setSetlistid(0);
        song8.setSongid(7);
        song8.setPosition(7);
        song8.setMetronombpm(80);
        song8.setAudioOn(false);
        song8.setLyrics(getResources().getString(R.string.EmptyLyrics));
        song8.setLyricshasopen(false);
        song8.setPlaystarted(false);

        Songs song9 = songsrealm.createObject(Songs.class);
        song9.setTitle("Воздаяние");
        song9.setSetlistid(0);
        song9.setSongid(8);
        song9.setPosition(8);
        song9.setMetronombpm(120);
        song9.setAudioOn(false);
        song9.setLyrics("Текст песни Воздаяние");
        song9.setLyricshasopen(false);
        song9.setPlaystarted(false);

        Songs song10 = songsrealm.createObject(Songs.class);
        song10.setTitle("Судный день");
        song10.setSetlistid(0);
        song10.setSongid(9);
        song10.setPosition(9);
        song10.setMetronombpm(140);
        song10.setAudioOn(false);
        song10.setLyrics("Текст песни Судный день");
        song10.setLyricshasopen(false);
        song10.setPlaystarted(false);

        Songs song11 = songsrealm.createObject(Songs.class);
        song11.setTitle("Заготовка 6");
        song11.setSetlistid(0);
        song11.setSongid(10);
        song11.setPosition(10);
        song11.setMetronombpm(140);
        song11.setAudioOn(false);
        song11.setLyrics("Текст песни Заготовка 6");
        song11.setLyricshasopen(false);
        song11.setPlaystarted(false);

        Songs song12 = songsrealm.createObject(Songs.class);
        song12.setTitle("Заготовка 7");
        song12.setSetlistid(0);
        song12.setSongid(11);
        song12.setPosition(11);
        song12.setMetronombpm(135);
        song12.setAudioOn(false);
        song12.setLyrics("Текст песни Заготовка 7");
        song12.setLyricshasopen(false);
        song12.setPlaystarted(false);

        Songs song13 = songsrealm.createObject(Songs.class);
        song13.setTitle("Заготовка 8");
        song13.setSetlistid(0);
        song13.setSongid(12);
        song13.setPosition(12);
        song13.setMetronombpm(150);
        song13.setAudioOn(false);
        song13.setLyrics("Текст песни Заготовка 8");
        song13.setLyricshasopen(false);
        song13.setPlaystarted(false);

        for (int i = 0; i < 7; i++) {
            int startFrom = 13;
            Songs songNew = songsrealm.createObject(Songs.class);
            songNew.setTitle(getResources().getString(R.string.EmptySongName));
            songNew.setSetlistid(0);
            songNew.setSongid(startFrom + i);
            songNew.setPosition(startFrom + i);
            songNew.setMetronombpm(80);
            songNew.setAudioOn(false);
            songNew.setLyrics(getResources().getString(R.string.EmptyLyrics));
            songNew.setLyricshasopen(false);
            songNew.setPlaystarted(false);
        }
//===================Other songs added for Test, on second Set List:
        Songs song1_1 = songsrealm.createObject(Songs.class);
        song1_1.setTitle("One Against All");
        song1_1.setSetlistid(1);
        song1_1.setSongid(20);
        song1_1.setPosition(0);
        song1_1.setMetronombpm(145);
        song1_1.setAudioOn(false);
        song1_1.setAudiofile(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
                + "/Amon Amarth/Amon Amarth - One Against All [Deathcoreanstvo] (1).mp3");
        song1_1.setLyrics(Amon_Amarth_One_Against_All_lyrics);
        song1_1.setLyricshasopen(false);
        song1_1.setPlaystarted(false);

        Songs song1_2 = songsrealm.createObject(Songs.class);
        song1_2.setTitle("We shall destroy");
        song1_2.setSetlistid(1);
        song1_2.setSongid(21);
        song1_2.setPosition(1);
        song1_2.setMetronombpm(122);
        song1_2.setAudioOn(false);
        song1_2.setAudiofile(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
                + "/Amon Amarth/Amon Amarth - We Shall Destroy (1).mp3");
        song1_2.setLyrics(Amon_Amarth_We_Shall_Destroy_lyrics);
        song1_2.setLyricshasopen(false);
        song1_2.setPlaystarted(false);

        Songs song1_3 = songsrealm.createObject(Songs.class);
        song1_3.setTitle("Cry of blackbirds");
        song1_3.setSetlistid(1);
        song1_3.setSongid(22);
        song1_3.setPosition(2);
        song1_3.setMetronombpm(173);
        song1_3.setAudioOn(false);
        song1_3.setAudiofile(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
                + "/Amon Amarth/Amon Amarth-Cry Of The Black Birds Vid.mp3");
        song1_3.setLyrics(Amon_Amarth_Cry_of_blackbirds_lyrics);
        song1_3.setLyricshasopen(false);
        song1_3.setPlaystarted(false);

        Songs song1_4 = songsrealm.createObject(Songs.class);
        song1_4.setTitle("At down's First light");
        song1_4.setSetlistid(1);
        song1_4.setSongid(23);
        song1_4.setPosition(3);
        song1_4.setMetronombpm(160);
        song1_4.setAudioOn(false);
        song1_4.setLyrics(Amon_Amarth_At_downs_First_light_lyrics);
        song1_4.setLyricshasopen(false);
        song1_4.setPlaystarted(false);

        Songs song1_5 = songsrealm.createObject(Songs.class);
        song1_5.setTitle("First Kill");
        song1_5.setSetlistid(1);
        song1_5.setSongid(24);
        song1_5.setPosition(4);
        song1_5.setMetronombpm(100);
        song1_5.setAudioOn(false);
        song1_5.setLyrics(Amon_Amarth_First_Kill_lyrics);
        song1_5.setLyricshasopen(false);
        song1_5.setPlaystarted(false);

//===================Other songs added for Test, on 3 Set List:
        Songs song2_1 = songsrealm.createObject(Songs.class);
        song2_1.setTitle("Eagles Fly's alone");
        song2_1.setSetlistid(2);
        song2_1.setSongid(25);
        song2_1.setPosition(0);
        song2_1.setMetronombpm(126);
        song2_1.setAudioOn(false);
        song2_1.setLyrics(Arch_Enemy_Eagles_Flys_alone);
        song2_1.setLyricshasopen(false);
        song2_1.setPlaystarted(false);

        Songs song2_2 = songsrealm.createObject(Songs.class);
        song2_2.setTitle("Avalanche");
        song2_2.setSetlistid(2);
        song2_2.setSongid(26);
        song2_2.setPosition(1);
        song2_2.setMetronombpm(192);
        song2_2.setAudioOn(false);
        song2_2.setLyrics(Arch_Enemy_Avalanche);
        song2_2.setLyricshasopen(false);
        song2_2.setPlaystarted(false);

        Songs song2_3 = songsrealm.createObject(Songs.class);
        song2_3.setTitle("Ravenous");
        song2_3.setSetlistid(2);
        song2_3.setSongid(27);
        song2_3.setPosition(2);
        song2_3.setMetronombpm(175);
        song2_3.setAudioOn(false);
        song2_3.setLyrics(Arch_Enemy_Ravenous);
        song2_3.setLyricshasopen(false);
        song2_3.setPlaystarted(false);

        Songs song2_4 = songsrealm.createObject(Songs.class);
        song2_4.setTitle("The world is Your's");
        song2_4.setSetlistid(2);
        song2_4.setSongid(28);
        song2_4.setPosition(3);
        song2_4.setMetronombpm(180);
        song2_4.setAudioOn(false);
        song2_4.setLyrics(Arch_Enemy_The_world_is_Yours);
        song2_4.setLyricshasopen(false);
        song2_4.setPlaystarted(false);

        Songs song2_5 = songsrealm.createObject(Songs.class);
        song2_5.setTitle("Taking back my soul");
        song2_5.setSetlistid(2);
        song2_5.setSongid(29);
        song2_5.setPosition(4);
        song2_5.setMetronombpm(140);
        song2_5.setAudioOn(false);
        song2_5.setLyrics(Arch_Enemy_Taking_back_my_soul);
        song2_5.setLyricshasopen(false);
        song2_5.setPlaystarted(false);

        Songs song2_6 = songsrealm.createObject(Songs.class);
        song2_6.setTitle("As the pages burn");
        song2_6.setSetlistid(2);
        song2_6.setSongid(30);
        song2_6.setPosition(5);
        song2_6.setMetronombpm(210);
        song2_6.setAudioOn(false);
        song2_6.setLyrics(Arch_Enemy_As_the_pages_burn);
        song2_6.setLyricshasopen(false);
        song2_6.setPlaystarted(false);

        songsrealm.commitTransaction();
        songsrealm.close();
    }
}