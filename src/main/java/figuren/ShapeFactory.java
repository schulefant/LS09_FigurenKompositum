package figuren;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import figuren2D.*;
import figuren3D.*;

public class ShapeFactory {
	
	public static Figur2D create2DShape(TwoDFig type, List<Double> dimensions) throws IllegalArgumentException{
		switch( type ) {
		case TRIANGLE: 
			if(dimensions.size()>=3)
				return new Dreieck(dimensions.get(0),dimensions.get(1),dimensions.get(2));
			break;
		case CIRCLE: 
			if(!dimensions.isEmpty())
				return new Kreis(dimensions.get(0));
			break;
		case RECTANGLE: 
			if(dimensions.size()>=2)
				return new Rechteck(dimensions.get(0),dimensions.get(1));
			break;
		case POLYGON: 
			if(dimensions.size()>=2) {
				double anzahl = dimensions.get(1);
				return new N_Eck(dimensions.get(0),(int)anzahl );
			}
			break;
		case STAR:
			if(dimensions.size()>=3) {
				double anzahl = dimensions.get(2);				
				return new Stern(dimensions.get(0),dimensions.get(1),(int)anzahl);
			}
			break;
		case STICKFIGURE:
			if(dimensions.size()>=6) {
				Kreis k = (Kreis) create2DShape(TwoDFig.CIRCLE , dimensions.subList(0,1));
				Dreieck d = (Dreieck) create2DShape(TwoDFig.TRIANGLE , dimensions.subList(1, 4));
				Rechteck r = (Rechteck) create2DShape(TwoDFig.RECTANGLE , dimensions.subList(4, 6));
				return new Maennchen(k, d, r);
			}
			break;
		}
		throw new IllegalArgumentException();
	}
	
	public static Figur3D create3DShape(ThreeDFig type, List<Double> dimensions) throws IllegalArgumentException{
		double anzahl=0;

		switch( type ) {
		case SPHERE: 
			if(!dimensions.isEmpty())
				return new Kugel(dimensions.get(0));
			break;
		case CYLINDER: 
			if(dimensions.size()>1) {
				return new GeradesPrisma<Kreis>(dimensions.get(0),new Kreis(dimensions.get(1)));
			}
			break;
		case REGULARPRISM: 
			if(dimensions.size()>2) {
				anzahl = dimensions.get(2);
				return new GeradesPrisma<N_Eck>(dimensions.get(0),new N_Eck(dimensions.get(1), (int)anzahl));
			}
			break;
		case REGULARPYRAMID: 
			if(dimensions.size()>2) {
				anzahl = dimensions.get(2);
				return new RegelmaessigeGeradePyramide(dimensions.get(0),new N_Eck(dimensions.get(1), (int)anzahl));
			}
			break;
		case CONE: 
			if(dimensions.size()>1) {
				return new KreisKegel(dimensions.get(0),dimensions.get(1));
			}
			break;
		default:
			break;
		}
		throw new IllegalArgumentException();
	}
	
	public static Figur3D create3DShapeWithBase(ThreeDFig type, Figur2D base, List<Double> dimensions) throws IllegalArgumentException{
		switch( type ) {
		case PRISM: 
			if(dimensions.size()==1) {
				return new GeradesPrisma<Figur2D>(dimensions.get(0),base);
			}
			break;
		case CONE: 
			if(dimensions.size()>1 && base.getClass().equals(Kreis.class)) {
				return new KreisKegel(dimensions.get(0),(Kreis)base);
			}
			break;
		case REGULARPRISM: 
			if(dimensions.size()>1 && base.getClass().equals(N_Eck.class)) {
				return new GeradesPrisma<N_Eck>(dimensions.get(0),(N_Eck)base);
			}
			break;
		case REGULARPYRAMID: 
			if(dimensions.size()>1 && base.getClass().equals(N_Eck.class)) {
				return new RegelmaessigeGeradePyramide(dimensions.get(0),(N_Eck)base);
			}
			break;
		default:
			break;
		}
		throw new IllegalArgumentException();
	}

	public void writeAll( List<ICSVString> figs, Path p) throws IOException {
		for(ICSVString f: figs) {
			Files.write(p, Arrays.asList(f.toCSVString().split(";")), StandardOpenOption.APPEND);
		}
	}
	public List<Figur2D> readAllTwoDShapes(Path p){
		List<Figur2D> l = new ArrayList<>();
		return l;
	}
	public List<Figur3D> readAllThreeDShapes(Path p) throws IOException{
		List<Figur3D> l = new ArrayList<>();
		List<String> strl = Files.readAllLines(p);
		for(String str : strl) {
			String[] dims = str.split(";");
			ThreeDFig tdf = ThreeDFig.valueOf(dims[0]);
			List<String> strDim = Arrays.asList(dims);
			List<Double> dblL = new ArrayList<>();
			strl.remove(0);
			for(String s: strDim)
				dblL.add(Double.parseDouble(s));
			l.add(create3DShape(tdf, dblL));
		}
		return l;
	}
}
