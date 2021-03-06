package eu.amidst.tutorial.usingAmidst.models;



import COM.hugin.HAPI.ExceptionHugin;
import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.io.DataStreamLoader;
import eu.amidst.core.models.BayesianNetwork;
import eu.amidst.core.variables.StateSpaceTypeEnum;
import eu.amidst.flinklink.core.data.DataFlink;
import eu.amidst.flinklink.core.io.DataFlinkLoader;
import eu.amidst.latentvariablemodels.staticmodels.FactorAnalysis;
import eu.amidst.latentvariablemodels.staticmodels.GaussianMixture;
import eu.amidst.latentvariablemodels.staticmodels.Model;
import org.apache.flink.api.java.ExecutionEnvironment;

import java.io.IOException;

/**
 * Created by rcabanas on 23/05/16.
 */
public class StaticModelLearning {
	public static void main(String[] args) throws ExceptionHugin, IOException {


		String path = "datasets/static/noclassdata/";


		DataStream<DataInstance> data = DataStreamLoader.open(path+"data0.arff");

		// Set-up Flink session (simple configuration)
		//final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
		// Load the data stream
		//DataFlink<DataInstance> data = DataFlinkLoader.open(env, path+"dataFlink0.arff", false);

		data.getAttributes().forEach(att -> {
			String name = att.getName();
			StateSpaceTypeEnum type = att.getStateSpaceType().getStateSpaceTypeEnum();
			System.out.println(name +" "+type.name());
		});

		// Build the model
		Model model =
				new CustomModel(data.getAttributes());
		//		.setNumberOfLatentVariables(10);
		//		.setDiagonal(true)
		//		.setNumStatesHiddenVar(2);

		// Learn the distributions
		model.updateModel(data);

		// Obtain the learned BN
		BayesianNetwork bn = model.getModel();

		// Print the BN
		System.out.println(bn);

		//Update the model with new information
		for(int i=1; i<12; i++) {
			data = DataStreamLoader.open(path+"data"+i+".arff");
			//data = DataFlinkLoader.open(env, path+"dataFlink0.arff", false);
			model.updateModel(data);
			System.out.println(model.getModel());
		}




	}

}
