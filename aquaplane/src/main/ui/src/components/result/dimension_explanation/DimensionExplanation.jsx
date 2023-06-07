import React from 'react'
import { Breadcrumbs } from '@mui/material';
import ArrowBackIosNewIcon from '@mui/icons-material/ArrowBackIosNew';

const DimensionExplanation = (props) => {
  const data = props.data;
  const dimension = props.dimension;
  const setDimension = props.setDimension;

  function createLinks(text, highlight, setDimension) {
    const parts = text.split(new RegExp(`(${highlight})`, 'gi'));
    return <span className='colored_link__container'>{parts.map((part, i) => 
            part.toLowerCase() === highlight.toLowerCase() ? <button className='link' key={i} onClick={() => setDimension(highlight)}>{part}</button> : part
        )}
    </span>;
  }

  function createDisabledLink(text, highlight) {
    const parts = text.split(new RegExp(`(${highlight})`, 'gi'));
    return <span className='grayed_out_link__container'>{parts.map((part, i) => 
            part.toLowerCase() === highlight.toLowerCase() ? <button className='link' key={i}>{part}</button> : part
        )}
    </span>;
  }

  function hasExplanationsForApproaches(subdimension) {
    for (let value of Object.values(data.subdimensions[subdimension].approaches)) {
      if (value.explanations.length) {
        return true;
      }
    }
  }

  function hasApproaches(subdimension) {
    if (data.subdimensions[subdimension].approaches) {
      return true;
    }
    return false;
  }

  return (
    <div className='result__container'>
        <div className='breadcrumb__container'>
          <Breadcrumbs separator="›" aria-label="breadcrumb">
            <button className='link' type='button' onClick={() => setDimension('')}>Overall</button>
            <button className='link' type='button' onClick={() => setDimension(dimension)}>{dimension}</button>
          </Breadcrumbs>
        </div>
        <div className="data__container">
            <div className='back_button__container'>
              <button className='link' onClick={() => setDimension('')}>
                <ArrowBackIosNewIcon fontSize='inherit'></ArrowBackIosNewIcon>
                &nbsp;
                Back
              </button>
            </div>
            <h3>Explanation</h3>
            <h4>{dimension}</h4>
            <br/>
            {
                Object.keys(data.subdimensions).map((key, index) => {
                    if (hasApproaches(key) && hasExplanationsForApproaches(key)) {
                      return (
                        <div key={index} className='explanation__container'>
                          <p>{createLinks(data.subdimensions[key].explanation, key, setDimension)}</p>
                        </div>
                      );
                    } else {
                      return (
                        <div key={index} className='explanation__container'>
                          <p>{createDisabledLink(data.subdimensions[key].explanation, key)}</p>
                        </div>
                      );
                    }
                })
            }
        </div>
    </div>
  )
}

export default DimensionExplanation