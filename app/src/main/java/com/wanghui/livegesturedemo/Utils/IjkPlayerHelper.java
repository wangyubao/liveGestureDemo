package com.wanghui.livegesturedemo.Utils;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.net.Uri;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by dell on 2017/3/28.
 */

public class IjkPlayerHelper implements TextureView.SurfaceTextureListener {
    public static final int IJK_EVENT_PREPARED = 0;
    public static final int IJK_EVENT_ERROR = 1;
    public static final int IJK_EVENT_BUFFER = 2;

    public static final int IJK_EVENT_COMPLETED = 3;
    public static final int IJK_EVENT_SEEKCOMPLETED = 4;
    private static IjkPlayerHelper instance;
    private ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    private String mPlayer = "";
    private WeakReference<SurfaceHolder> mReference;

    public static IjkPlayerHelper getInstance() {
        if (instance == null) {
            synchronized (IjkPlayerHelper.class) {
                if (instance == null) {
                    instance = new IjkPlayerHelper();
                }
            }
        }
        return instance;
    }

    public IjkMediaPlayer open(String url, SurfaceHolder holder, boolean isLive) {
        this.mReference = new WeakReference<>(holder);
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        IjkMediaPlayer player = new IjkMediaPlayer();
        if (isLive) {
            setLiveIjkPlayerParams(player);

        } else {
            player.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", "48");

            player.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(IMediaPlayer iMediaPlayer) {
                    if (mIjkPlayerCallBackListener != null)
                        mIjkPlayerCallBackListener.eventCallBack(IJK_EVENT_COMPLETED, iMediaPlayer, 0, 0);
                }
            });
            player.setOnSeekCompleteListener(new IMediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(IMediaPlayer iMediaPlayer) {
                    if (mIjkPlayerCallBackListener != null)
                        mIjkPlayerCallBackListener.eventCallBack(IJK_EVENT_SEEKCOMPLETED, iMediaPlayer, 0, 0);
                }
            });
        }
        setIJKPLayerListener(player);
        LogUtil.i("dfsd", url);
        try {
            player.setDataSource(url.trim());
            if (mReference != null && mReference.get() != null) {
                player.setDisplay(mReference.get());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setScreenOnWhilePlaying(true);
        player.prepareAsync();
//        player.start();
        return player;
    }

    public IjkMediaPlayer openLocal(Context context, Uri uri,  SurfaceHolder holder, boolean isLive) {
        this.mReference = new WeakReference<>(holder);
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        IjkMediaPlayer player = new IjkMediaPlayer();
        if (isLive) {
            setLiveIjkPlayerParams(player);

        } else {
            player.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", "48");

            player.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(IMediaPlayer iMediaPlayer) {
                    if (mIjkPlayerCallBackListener != null)
                        mIjkPlayerCallBackListener.eventCallBack(IJK_EVENT_COMPLETED, iMediaPlayer, 0, 0);
                }
            });
            player.setOnSeekCompleteListener(new IMediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(IMediaPlayer iMediaPlayer) {
                    if (mIjkPlayerCallBackListener != null)
                        mIjkPlayerCallBackListener.eventCallBack(IJK_EVENT_SEEKCOMPLETED, iMediaPlayer, 0, 0);
                }
            });
        }
        setIJKPLayerListener(player);
//        LogUtil.i("dfsd", url);
        try {
            player.setDataSource(context, uri);
            if (mReference != null && mReference.get() != null) {
                player.setDisplay(mReference.get());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setScreenOnWhilePlaying(true);
        player.prepareAsync();
//        player.start();
        return player;
    }

    public IjkMediaPlayer openWithTextureView(String url, Surface surface) {
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        IjkMediaPlayer player = new IjkMediaPlayer();
        setLiveIjkPlayerParams(player);
        setIJKPLayerListener(player);
        try {
            player.setDataSource(url.trim());
            player.setSurface(surface);
        } catch (Exception e) {
            e.printStackTrace();
        }

        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setScreenOnWhilePlaying(true);
        player.prepareAsync();
        return player;
    }

    private void setIJKPLayerListener(IjkMediaPlayer player) {
        player.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                if (mIjkPlayerCallBackListener != null)
                    mIjkPlayerCallBackListener.eventCallBack(IJK_EVENT_PREPARED, iMediaPlayer, 0, 0);
            }
        });
        player.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int var1, int var2) {
                if (mIjkPlayerCallBackListener != null)
                    mIjkPlayerCallBackListener.eventCallBack(IJK_EVENT_ERROR, iMediaPlayer, var1, var2);
                return false;
            }
        });
        player.setOnBufferingUpdateListener(new IMediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int var) {
                if (mIjkPlayerCallBackListener != null) {
                    if (var > 95)
                        var = 100;
                    mIjkPlayerCallBackListener.eventCallBack(IJK_EVENT_BUFFER, iMediaPlayer, var, 0);
                }
            }
        });
    }

    private void setLiveIjkPlayerParams(IjkMediaPlayer player) {
        player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
        player.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);
        player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
        player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "analyzeduration", "2000000");
        player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "probsize", "4096");
        player.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 0);
        player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max_cached_duration", 3000);
        player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "infbuf", 1);

//        player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 0);

//            player.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzeduration", 50000);
//            player.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "fflags", "nobuffer");
//            player.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probesize", "4096");//解码内存分配，部分流要设置的足够大才能成功解码，不然会导致只有图像没有声音。但是设置太大会导致解码延迟
//            player.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_frame", 0);
    }


    public void endPlayer(final IjkMediaPlayer player) {
        if (player == null)
            return;
        mIjkPlayerCallBackListener = null;
        if (mPlayer.equals(player + "")) {
            return;
        }
        mPlayer = player + "";
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                if (player != null) {
                    player.setDisplay(null);
                    player.reset();
                    player.release();
                }
            }
        });
        IjkMediaPlayer.native_profileEnd();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    public interface IjkPlayerCallBackListener {
        void eventCallBack(int event, IMediaPlayer iMediaPlayer, int var1, int var2);
    }

    IjkPlayerCallBackListener mIjkPlayerCallBackListener = null;

    public void setIjkPlayerCallBackListener(IjkPlayerCallBackListener listener) {
        mIjkPlayerCallBackListener = listener;
    }

    public interface IjkCameraPlayerCallBackListener {
        void eventCallBack(int event, IMediaPlayer iMediaPlayer, int var1, int var2);
    }

    IjkCameraPlayerCallBackListener mIjkCameraPlayerCallBackListener = null;

    public void setmIjkCameraPlayerCallBackListener(IjkCameraPlayerCallBackListener mIjkCameraPlayerCallBackListener) {
        this.mIjkCameraPlayerCallBackListener = mIjkCameraPlayerCallBackListener;
    }
}
