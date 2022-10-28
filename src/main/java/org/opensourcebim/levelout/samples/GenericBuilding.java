package org.opensourcebim.levelout.samples;


interface GetPolygon
{
	void definePolygon(int num);
}

interface Outputfile
{
	void createOutputfile();
}

abstract class CreateList{
	
	public abstract void getList() ;

}

public class GenericBuilding extends CreateList implements GetPolygon, Outputfile  {

	@Override
	public void createOutputfile() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void definePolygon(int num) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getList() {
		// TODO Auto-generated method stub
		
	}
	// private CitygmlBuilding class2 = new CitygmlBuilding();

}
