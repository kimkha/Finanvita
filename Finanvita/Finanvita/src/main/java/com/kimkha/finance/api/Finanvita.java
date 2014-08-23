package com.kimkha.finance.api;

import android.content.Context;
import com.kimkha.finance.utils.StringUtils;
import retrofit.RestAdapter;
import retrofit.client.Response;

import java.io.IOException;

public class Finanvita
{
    private static Finanvita instance;
    private Context context;
    private FService service;

    public Finanvita(Context context)
    {
        this.context = context.getApplicationContext();

        final RestAdapter restAdapter = new RestAdapter.Builder().setServer(getBaseURL()).build();
        service = restAdapter.create(FService.class);
    }

    public static Finanvita getDefault(Context context)
    {
        if (instance == null)
            instance = new Finanvita(context);
        return instance;
    }

    public static String parseResponseBody(Response response) throws IOException
    {
        String body = null;
        try
        {
            body = StringUtils.readInputStream(response.getBody().in());
        }
        finally
        {
            try
            {
                response.getBody().in().close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return body;
    }

    public FService getService()
    {
        return service;
    }

    protected String getBaseURL()
    {
        return "";
    }
}