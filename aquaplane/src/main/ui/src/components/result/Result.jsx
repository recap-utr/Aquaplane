import React, { useState } from 'react';
import DimensionExplanation from './dimension_explanation/DimensionExplanation';
import OverallExplanation from './overall_explanation/OverallExplanation';
import './result.css'
import SubdimensionExplanation from './subdimension_explanation/SubdimensionExplanation';

const Result = (props) => {
    const data = props.data;
    const [dimension, setDimenson] = useState('');

    function downloadJSON() {
        const fileName = "aquaplane.json";
        const blob = new Blob([JSON.stringify(data, null, 4)], { type: "text/json" });
        const jsonURL = window.URL.createObjectURL(blob);
        const link = document.createElement("a");
        document.body.appendChild(link);
        link.href = jsonURL;
        link.setAttribute("download", fileName);
        link.click();
        document.body.removeChild(link);
    }

    return (
        <div>
            {
                {
                    '': <OverallExplanation 
                            data={data} 
                            setDimension={setDimenson}
                        />,
                    'Logic Quality': <DimensionExplanation 
                                        dimension="Logic Quality" 
                                        data={data.argumentQuality["Logic Quality"]} 
                                        setDimension={setDimenson}
                                     />,
                    'Rhetoric Quality': <DimensionExplanation 
                                            dimension="Rhetoric Quality" 
                                            data={data.argumentQuality["Rhetoric Quality"]} 
                                            setDimension={setDimenson}
                                        />,
                    'Dialectic Quality': <DimensionExplanation 
                                            dimension="Dialectic Quality" 
                                            data={data.argumentQuality["Dialectic Quality"]} 
                                            setDimension={setDimenson}
                                         />,
                    'Local Sufficiency': <SubdimensionExplanation 
                                            dimension="Logic Quality" 
                                            subdimension="Local Sufficiency" 
                                            data={data.argumentQuality["Logic Quality"].subdimensions["Local Sufficiency"]} 
                                            setDimension={setDimenson}
                                         />,
                    'Local Relevance': <SubdimensionExplanation 
                                            dimension="Logic Quality" 
                                            subdimension="Local Relevance" 
                                            data={data.argumentQuality["Logic Quality"].subdimensions["Local Relevance"]} 
                                            setDimension={setDimenson}
                                        />,
                    'Local Acceptability': <SubdimensionExplanation 
                                            dimension="Logic Quality" 
                                            subdimension="Local Acceptability" 
                                            data={data.argumentQuality["Logic Quality"].subdimensions["Local Acceptability"]} 
                                            setDimension={setDimenson}
                                        />,
                    'Global Sufficiency': <SubdimensionExplanation 
                                            dimension="Dialectic Quality" 
                                            subdimension="Global Sufficiency" 
                                            data={data.argumentQuality["Dialectic Quality"].subdimensions["Global Sufficiency"]} 
                                            setDimension={setDimenson}
                                        />,
                    'Global Relevance': <SubdimensionExplanation 
                                            dimension="Dialectic Quality" 
                                            subdimension="Global Relevance" 
                                            data={data.argumentQuality["Dialectic Quality"].subdimensions["Global Relevance"]} 
                                            setDimension={setDimenson}
                                        />,
                    'Global Acceptability': <SubdimensionExplanation 
                                                dimension="Dialectic Quality" 
                                                subdimension="Global Acceptability" 
                                                data={data.argumentQuality["Dialectic Quality"].subdimensions["Global Acceptability"]} 
                                                setDimension={setDimenson}
                                            />,
                    'Emotional Appeal': <SubdimensionExplanation 
                                            dimension="Rhetoric Quality" 
                                            subdimension="Emotional Appeal" 
                                            data={data.argumentQuality["Rhetoric Quality"].subdimensions["Emotional Appeal"]} 
                                            setDimension={setDimenson}
                                        />,
                    'Credibility': <SubdimensionExplanation 
                                        dimension="Rhetoric Quality" 
                                        subdimension="Credibility" 
                                        data={data.argumentQuality["Rhetoric Quality"].subdimensions["Credibility"]} 
                                        setDimension={setDimenson}
                                    />,
                    'Clarity': <SubdimensionExplanation 
                                        dimension="Rhetoric Quality" 
                                        subdimension="Clarity" 
                                        data={data.argumentQuality["Rhetoric Quality"].subdimensions["Clarity"]} 
                                        setDimension={setDimenson}
                                    />,
                    'Appropriateness': <SubdimensionExplanation 
                                            dimension="Rhetoric Quality" 
                                            subdimension="Appropriateness" 
                                            data={data.argumentQuality["Rhetoric Quality"].subdimensions["Appropriateness"]} 
                                            setDimension={setDimenson}
                                        />,
                    'Arrangement': <SubdimensionExplanation 
                                        dimension="Rhetoric Quality" 
                                        subdimension="Arrangement" 
                                        data={data.argumentQuality["Rhetoric Quality"].subdimensions["Arrangement"]} 
                                        setDimension={setDimenson}
                                    />,
                }[dimension]
            }
            <div className='button_row__container'>
                <button type="reset" className='btn'>Clear all</button>
                &nbsp;&nbsp;&nbsp;
                <button type="button" className='btn btn-primary' onClick={downloadJSON}>Download json</button>
            </div>
        </div>
    )
}
  
export default Result