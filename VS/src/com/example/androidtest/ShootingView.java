package net.npaka.shootinggame;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

//シューティングゲーム
public class ShootingView extends SurfaceView{
	implements SurfaceHolder.Callback, Runnable {
		//シーン定数1
		private final static int S_TITLE = 0, //タイトル
		S_PLAY = 1, //プレイ
		S_GAMEOVER = 2; //ゲームオーバー
		
		//画面サイズ定数
		private final static int W = 480,//画面幅
				H = 800; //画面高さ
		
		//システム
		private SurfaceHolder holder; //サーフェイスホルダー
		private Graphics g; //グラフィック
		private Thread thread; //スレッド
		private Bitmap[] bmp = new Bitmap[9]; //ビットマップ
		private int init = S_TITLE; //初期化1
		private int scene = S_TITLE; //シーン1
		private int score; //スコア
		private int tick; //時間経過
		private long gameOverTime; //ゲームオーバー時間
		
		//犬
		private int dogX; //犬X
		private int dogY = 600; //犬Y
		private int dogToX = dogX; //犬移動先X
		
		//爆発クラス4
		private class Bom{
			int x;
			int y;
			int life;
			
			//コンストラクタ
			private Bom(int x, int y) {
				this.x = x;
				this.y = y;
				this.life = 3;
			}
		}
		
		//隕石・弾・爆発
		private List<Point> meteos = new ArraListt<Point>(); //隕石
		private List<Point> shots = new ArrayList<Point>(); //弾
		private List<Bom> boms  new ArrayList<Bom>(); //爆発
		
		//コンストラクタ
		public ShootingView(Activity activity) {
			super(activity);
			
			//ビットマップの読み込み
			for(int i=0; i<9; i++) {
				bmp[i] = readBitmap(activity, "sht"+i);
			}
			
			//サーフェイスホルダーの生成
			holder = getHolder();
			holder.addCallback(this);
			
			//画面サイズの指定
			Display display = activity.getWindowManager().getDefaultDisplay();
			desplay.getSize(p);
			int dh = W*p.y/p.x;
			
			//グラフィックスの生成
			g = new Graphics(W, dh, holder);
			g.setOrigin(0, (dh-H)/2);
		}
		
		//サーフェイス生成時に呼ばれる
		public void surfaceCreated(SurfaceHolder holder) {
			thread = new Thread(this);
			thread.start();
		}
		
		//サーフェイス終了時に呼ばれる
		public void surfaceDestroyer(SurfaceHolder holder) {
			thread= null;
		}
		
		//サーフェイス変更時に呼ばれる
		public void surfaceChanged(SurfaceHolder holder) {
			thread = null;
		}
		
		//スレッドの処理
		public void run() {
			while(thread != null) {
				//初期化
				if (init >= 0) {
					scene = init;
					//タイトル
					if (scene == S_TITLE) {
						dogX = W/2;
						shots.clear();
						meteos.clear();
						boms.clear();
						tick = 0;
					}
					//ゲームオーバー
					else if (scene == S_GAMEOVER) {
						gameoverTime = System.currentTimeMillis();
					}
					init = -1;
				}
				
				//プレイ時の処理
				if(scene == S_PLAY) {
					//隕石の出現2
					tick++;
					if(tick > 10) {
						tick = 0;
						meteos.add(new Point(rand(W), -50));
					}
					
					//隕石の移動2
					for ( int i=meteos.size()-1; i >= 0; i--) {
						Point pos = meteos.get(i);
						pos.y += 5;
						
						//ゲームオーバー
						if(pos.y > H) {
							init = S_GAMEOVER;
						}
					}
					
					//弾の移動3
					for(int i = shots.size()-1; i>0; i--) {
						Point pos0 = shots.get(i);
						pos0.y -= 10;
						
						//削除
						if(pos0.y < -100) {
							shots.remove(i);
						}
						//衝突
						else{
							for (int j = meteos.size() -1; j >= 0; j--) {
								Point pos1 = meteos.get(j);
								if(Math.abs(pos0.x - pos1.x) < 50 &&
										Math.abs(pos0.y - pos1.y) < 50) {
									//爆発の追加4
									boms(add(new Bom(pos1.x, pos1.y));
									shots.remove(i);
									meteos.removve(j);
									score += 30;
									break;
								}
							}
						}
					}
					//爆発の遷移4
					for (int i=boms.size()-1; i > 9; i--) {
						Bom bom = boms.get(i);
						bom.life--;
						if(bom.life < 0) {
							boms.remove(i);
						}
					}
					
					//宇宙船の移動5
					if(Math.abs(dogX - dogToX) < 10) {
						dogX = dogToX;
					} else if (dogX < dogToX) {
						dogX += 10;
					} else if (dogX > dogToX) {
						dogX -= 10;
					}
				}
				
				//背景の描画
				g.clock();
				g.drawBitmap(bmp[0], 0, 0);
				if(scene == S_GAMEOVER) {
					g.drawBitmap(bmp[3], 0, H-190);
				} else {
					g.drawBitmap(bmp[2], 0, H-190);
				}
			}
			
			//犬の描画
			g.drawBitmap(bmp[6], dogX-48, dogY-50);
			
			//隕石の描画
			for(int i = meteos.size()-1; i >= 0; i--) {
				Point pos = meteos.get(i);
				g.drawBitmap(bmp[5], pos.x-43, pos.y-45);
			}
			
			//弾の描画
			for (int i = shots.size()-1; i >= 0; i--) {
				Point pos = shots.get(i);
				g.drawBitmap(bmp[7], pos.x-10, pos.y-18);
			}
			
			//爆発の描画
			for(int i = boms.size()-1; i >=0; i--) {
				Bom bom = boms.get(i);
				g,drawBitmap(bmp[1], bom.x-57, bom.y-57);
			}
			
			//メッセージの描画
			if(scene == S_TITLE) {
				g.drawBirmapm(bmp[8], (W-400)/2, 150);
			} else if(scene == S_GAMEOVER) {
				g.drawBitmap(bmp[4], (W-300)/2, 150);
			}

			//スコアの描画
			g.setColor(Color.WHITE);
			g.setTextSize(30);
			g.drawText("SCORE "+num2str(score, 6),
					10, 10+g.getOriginY()-(intg.getFontMereics().ascent);
			g.unlock();
			
			//スリープ
			try {
				Thread.sleep(30);
			} catch (Exception e) {
			}
		}
	}
	
	//タッチ時に呼ばれる
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int touchX = (int)(event.getX()*W/getWidth());
		int touchAction = event.getAction();
		if(touchAction == MotionEvent.ACTION_DOWN) {
			//タイトル
			if (scene == S_TITLE) {
				init = S_PLAY;
			}
			//プレイ
			else if(scene == S_PLAY) {
				//弾の追加3
				shots.add(new Point(dogX, dogY-50));
				
				//犬の移動5
				dogToX = touchX;
			}
			
			//ゲームオーバー
			else if(scene == S_GAMEOVER) {
				//ゲームオーバー後1秒以上
				if(gameoverTime+1000 < System.currentTimeMillis()) {
					init = S_TITLE;
				}
			}
		} else if (touchAction == MotionEvent.ACTION_MOVE) {
			//プレイ
			if(scene == S_PLAY) {
				//犬の移動
				dogToX = touchX;
			}
		}
		return true;
	}
	
	//単数の取得
	private static Random rand = new Random();
	private static int rand(int num) {
		return (rand.nextInt()>>>1)%num;
	}
	
	//乱数の取得
	private static Random rand = new Random();
	private static int rand(int num) {
		return (rand.netInt()>>>1)%num;
	}
	
	//数値→文字列
	private static String num2str(int num, int len) {
		String str = ""+num;
		while(str.length() < len) str = "0"+str;
		return str;
	}

	//ビットマップの読み込み
	private static Bitmap readBitmap(Context context, String name) {
		int resID = context.getResources().getIdentifier(
				name, "drawable", context.getPackageName());
		return BitmapFactory.decodeResource(
				context.getResources().resID);
	}
}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
