package com.project3;

import com.badlogic.gdx.graphics.g2d.Sprite;

	public class Coord
	{
		float x, y;

		static Coord polar (double r, double theta)
		{
			return new Coord((float)Math.cos(theta)*r, (float)Math.sin(theta)*r);
		}

		Coord (double x, double y)
		{
			this.x = (float)x; this.y = (float)y;
		}

		Coord rotated (double angle)
		{
				return Coord.polar(radius(), angle + theta());
		}

		float theta_deg ()
		{
			return (float)(theta() / (Math.PI*2) * 360);
		}

		float theta ()
		{
			return (float)Math.atan2(y, x);
		}

		Coord theta (double t)
		{
			return polar(radius(), t);
		}

		float radius ()
		{
			return (float)Math.sqrt(x*x+y*y);
		}

		Coord radius (double r)
		{
			return polar(r, theta());
		}

		Coord times (Coord o)
		{
			return new Coord(x*o.x, y*o.y);
		}

		Coord times (double d)
		{
			return times(new Coord(d,d));
		}

		Coord minus (Coord o)
		{
			return new Coord(x-o.x, y-o.y);
		}

		Coord plus (Coord o)
		{
			return new Coord(x+o.x, y+o.y);
		}

		Coord position (Sprite s)
		{
			s.setOriginBasedPosition(x, y);
			return this;
		}

		Coord rotation (Sprite s)
		{
			s.setRotation((float)(theta() / (Math.PI*2) * 360));
			return this;
		}

		public String toString ()
		{
			return "("+x+","+y+")";
		}
	}