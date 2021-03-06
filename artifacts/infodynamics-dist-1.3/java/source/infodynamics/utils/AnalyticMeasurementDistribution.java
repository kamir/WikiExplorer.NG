/*
 *  Java Information Dynamics Toolkit (JIDT)
 *  Copyright (C) 2012, Joseph T. Lizier
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package infodynamics.utils;

/**
 * Generic class to represent analytic distributions of info theoretic measurements under
 * some null hypothesis of a relationship between the variables.
 * This class is not designed to be creatable - it's children (which represent
 * various concrete distributions) are those which should be created.
 * 
 * @author Joseph Lizier (<a href="joseph.lizier at gmail.com">email</a>,
 * <a href="http://lizier.me/joseph/">www</a>)
 */
public class AnalyticMeasurementDistribution extends MeasurementDistribution {

	/**
	 * Construct the instance
	 * 
	 * @param actualValue observed value of the information-theoretic measure
	 * @param pValue p-value that surrogate measurements are greater than
	 *  the observed value.
	 */
	protected AnalyticMeasurementDistribution(double actualValue, double pValue) {
		super(actualValue, pValue);
	}
}
