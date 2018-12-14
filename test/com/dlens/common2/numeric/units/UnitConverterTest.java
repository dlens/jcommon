package com.dlens.common2.numeric.units;

import org.junit.Test;
import static org.junit.Assert.*;

public class UnitConverterTest {

	@Test
	public void testTime1() throws Exception {
		Unit week=new Unit(BaseUnitTime.Week);
		Unit day=new Unit(BaseUnitTime.Day);
		double val=UnitConverter.convertDouble(1.0, week, day);
		//This should be 7.0;
		assertEquals(7.0, val, 0);
		Unit decaWeek=new Unit(UnitPrefix.Deca, BaseUnitTime.Week);
		Unit centiYear=new Unit(UnitPrefix.Centi, BaseUnitTime.Year);
		val=UnitConverter.convertDouble(2.0, decaWeek, centiYear);
		assertEquals(20.0*7.0/(365.0)*100, val, 0);
	}
	
	@Test 
	public void testTemp1() throws Exception {
		Unit far=new Unit(BaseUnitTemperature.Fahrenheit);
		Unit cel=new Unit(BaseUnitTemperature.Celsisus);
		double val=UnitConverter.convertDouble(32, far, cel);
		assertEquals(0, val, 0);
		val=UnitConverter.convertDouble(0, cel, far);
		assertEquals(32, val, 0);
		Unit hunKel=new Unit(UnitPrefix.Hecto, BaseUnitTemperature.Kelvin);
		Unit tensF=new Unit(UnitPrefix.Deca, BaseUnitTemperature.Fahrenheit);
		val=UnitConverter.convertDouble(3.0, hunKel, tensF);
		assertEquals(8.06, val, 1e-10);
	}
	
	@Test
	public void testLength() throws Exception {
		Unit foot=new Unit(BaseUnitLength.Foot);
		Unit inch=new Unit(BaseUnitLength.Inch);
		double val=UnitConverter.convertDouble(2.0, foot, inch);
		assertEquals(24.0, val, 0);
		val=UnitConverter.convertDouble(2.0, new Unit(UnitPrefix.Kilo, BaseUnitLength.Meter),
				new Unit(UnitPrefix.Deci, BaseUnitLength.Mile));
		assertEquals(12.427, val, 1e-3);
	}
	
	@Test
	public void testWeight() throws Exception {
		double val=UnitConverter.convertDouble(2.0, new Unit(BaseUnitWeight.Pound), new Unit(BaseUnitWeight.Ounce));
		assertEquals(32.0, val, 0.0);
		val=UnitConverter.convertDouble(2.0,
				new Unit(UnitPrefix.Kilo, BaseUnitWeight.Gram),
				new Unit(UnitPrefix.Hecto, BaseUnitWeight.Pound));
		assertEquals(0.04409245, val, 1e-5);
	}
	
	@Test
	public void testVolume() throws Exception {
		double val;
		val=UnitConverter.convertDouble(3.0, new Unit(BaseUnitVolume.Gallon), new Unit(BaseUnitVolume.Quart));
		assertEquals(12.0, val, 0.0);
		val=UnitConverter.convertDouble(2.0, new Unit(BaseUnitVolume.Gallon), new Unit(BaseUnitVolume.Pint));
		assertEquals(16.0, val, 0.0);
		val=UnitConverter.convertDouble(5.0, new Unit(BaseUnitVolume.CubicFoot), new Unit(BaseUnitVolume.CubicInch));
		assertEquals(5*12*12.0*12.0, val, 0.0);
		val=UnitConverter.convertDouble(2.0, new Unit(BaseUnitVolume.CubicCentimeter), new Unit(BaseUnitVolume.CubicMeter));
		assertEquals(2e-6, val, 0.0);
		val=UnitConverter.convertDouble(3.0, new Unit(BaseUnitVolume.CubicYard), new Unit(BaseUnitVolume.Cup));
		assertEquals(9694.75325, val, 1e-4);
		val=UnitConverter.convertDouble(2.0, new Unit(BaseUnitVolume.Tablespoon), new Unit(BaseUnitVolume.Teaspoon));
		assertEquals(6.0, val, 0.0);
	}
	
	@Test
	public void testCurrencySimple() throws Exception {
		double val;
		val=UnitConverter.convertDouble(5.1, new Unit(BaseUnitCurrency.USDollar), new Unit(BaseUnitCurrency.USCent));
		assertEquals(510.0, val, 1e-10);
		val=UnitConverter.convertDouble(2.0, new Unit(BaseUnitCurrency.USDollar), new Unit(BaseUnitCurrency.Euro));
		assertEquals(1.50, val, 1e-10);
	}
}
