import React from 'react'
import { Breadcrumbs } from '@mui/material';
import ArrowBackIosNewIcon from '@mui/icons-material/ArrowBackIosNew';

const SubdimensionExplanation = (props) => {
  const data = props.data;
  const dimension = props.dimension;
  const subdimension = props.subdimension;
  const setDimension = props.setDimension;  

  return (
    <div className='result__container'>
        <div className='breadcrumb__container'>
          <Breadcrumbs separator="›" aria-label="breadcrumb">
            <button className='link' type='button' onClick={() => setDimension('')}>Overall</button>
            <button className='link' type='button' onClick={() => setDimension(dimension)}>{dimension}</button>
            <button className='link' type='button' onClick={() => setDimension(subdimension)}>{subdimension}</button>
          </Breadcrumbs>
        </div>
        <div className="data__container">
            <div className='back_button__container'>
              <button className='link' onClick={() => setDimension(dimension)}>
                <ArrowBackIosNewIcon fontSize='inherit'></ArrowBackIosNewIcon>
                &nbsp;
                Back
              </button>
            </div>
            <h3>Explanation</h3>
            <h4>{subdimension}</h4>
            <br/>
            {
                Object.keys(data.approaches).map((key, index) => {
                    return <div key={index} className='explanation__container'>
                        {
                          data.approaches[key].explanations.map((explanation, index) => 
                            <p key={index}>{explanation}</p>
                          )
                        }
                    </div>
                })
            }
        </div>
    </div>
  )
}

export default SubdimensionExplanation