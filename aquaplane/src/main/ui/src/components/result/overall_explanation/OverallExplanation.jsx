import React from 'react'
import { Breadcrumbs } from '@mui/material';

const OverallExplanation = (props) => {
  const data = props.data;
  const setDimension = props.setDimension;

  function createLinks(text, highlight, setDimension) {
    const parts = text.split(new RegExp(`(${highlight})`, 'gi'));
    return <span className='colored_link__container'>{parts.map((part, i) => 
            part.toLowerCase() === highlight.toLowerCase() ? <button className='link' key={i} onClick={() => setDimension(highlight)}>{part}</button> : part
        )}
    </span>;
  }

  return (
    <div className='result__container'>
        <div className='breadcrumb__container'>
          <Breadcrumbs separator="›" aria-label="breadcrumb">
            <button className='link' type='button' onClick={() => setDimension('')}>Overall</button>
          </Breadcrumbs>
        </div>
        <div className="data__container">
            <h3>Decision</h3>
            <p>{data.explanation}</p>
            <br/>
            <h3>Explanation</h3>
            {
                Object.keys(data.argumentQuality).map((key, index) => {
                    return (
                      <div key={index} className='explanation__container'>
                        <p>{createLinks(data.argumentQuality[key].explanation, key, setDimension)}</p>
                      </div>
                    );
                })
            }
        </div>
    </div>
  )
}

export default OverallExplanation