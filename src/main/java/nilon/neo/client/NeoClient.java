package nilon.neo.client;

import net.fabricmc.api.ClientModInitializer;



public class NeoClient implements ClientModInitializer  {

    private static boolean Client = false;

    @Override
    public void onInitializeClient() {
        Client = true;
        // basically a null function. doesn't do anything except reaffirm client status
    }

    public static boolean isClient(){
        return Client;
    }
}
